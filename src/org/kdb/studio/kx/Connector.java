package org.kdb.studio.kx;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.kx.type.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connector implements AutoCloseable {

    DataInputStream inputStream;
    OutputStream outputStream;
    Socket socket;

    byte[] b;
    int j;
    boolean a;
    int rxBufferSize;

    boolean closed = true;

    public void reconnect(boolean retry) throws IOException, K4Exception {
        socket = new Socket();
        socket.setReceiveBufferSize(1024 * 1024);
        socket.connect(new InetSocketAddress(host, port));
        io(socket);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write((up + (retry ? "\3" : "")).getBytes());
        dos.writeByte(0);
        dos.flush();
        outputStream.write(baos.toByteArray());
        byte[] bytes = new byte[2 + up.getBytes().length];
        if (1 != inputStream.read(bytes, 0, 1))
            if (retry)
                reconnect(false);
            else
                throw new K4Exception("Authentication failed");
        closed = false;
    }

    private String host;
    private int port;
    private String up;

    public Connector(String h, int p, String u) {
        host = h;
        port = p;
        up = u;
    }

    public boolean isClosed() {
        try {
            inputStream.available();
        } catch (IOException e) {
            return false;
        }
        return closed || socket == null || socket.isClosed();
    }

    void io(Socket s) throws IOException {
        s.setTcpNoDelay(true);
        inputStream = new DataInputStream(s.getInputStream());
        outputStream = s.getOutputStream();
        rxBufferSize = s.getReceiveBufferSize();
    }

    public void close() {
        try {
            // this will force k() to break out i hope
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                } finally {
                    inputStream = null;
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                } finally {
                    outputStream = null;
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        } finally {
            closed = true;
        }
    }

    public <T extends KBase> T query(KBase request, Class<T> type, Project project) throws Throwable {
        return ProgressManager.getInstance().run(new Task.WithResult<T, Exception>(project, "Executing query", true) {
            @Override
            protected T compute(@NotNull ProgressIndicator progressIndicator) throws Exception {
                progressIndicator.setText("Send query to server...");
                k(request);
                progressIndicator.checkCanceled();
                progressIndicator.setText("Receiving response...");
                try {
                    return type.cast(read(progressIndicator));
                } catch (K4Exception e) {
                    throw e;
                } catch (ProcessCanceledException e) {
                    throw new IOException("Cancelled by user.");
                } catch (Throwable thr) {
                    close();
                    throw thr;
                } finally {
                    b = null;
                }
            }

            @Override
            public void onCancel() {
                close();
                try {
                    reconnect(true);
                } catch (Exception e) {
                    Notifications.Bus.notify(new Notification("KDBStudio", "Server reconnection failed", e.getMessage(),  NotificationType.WARNING));
                }
                super.onCancel();
            }
        });
    }

    public <T extends KBase> T query(KBase request, Class<T> type, ProgressIndicator progressIndicator) throws Throwable {
        progressIndicator.setText("Send query to server...");
        k(request);
        progressIndicator.checkCanceled();
        try {
            return type.cast(read(progressIndicator));
        } catch (K4Exception e) {
            throw e;
        } catch (ProcessCanceledException e) {
            throw new IOException("Cancelled by user.");
        } catch (Throwable thr) {
            close();
            throw thr;
        } finally {
            b = null;
        }
    }

    public void onCancel() {
        close();
        try {
            reconnect(true);
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification("KDBStudio", "Server reconnection failed", e.getMessage(),  NotificationType.WARNING));
        }
    }

    protected void w(int i, KBase x) throws IOException {
        try (ByteArrayOutputStream baosBody = new ByteArrayOutputStream(); DataOutputStream dosBody = new DataOutputStream(baosBody);
             ByteArrayOutputStream baosHeader = new ByteArrayOutputStream(); DataOutputStream dosHeader = new DataOutputStream(baosHeader)) {
            x.serialise(dosBody);
            dosHeader.writeByte(0);
            dosHeader.writeByte(i);
            dosHeader.writeByte(0);
            dosHeader.writeByte(0);
            int msgSize = 8 + dosBody.size();
            Writer.write(dosHeader, msgSize);
            byte[] b = baosHeader.toByteArray();
            outputStream.write(b);
            b = baosBody.toByteArray();
            outputStream.write(b);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    protected KBase read(ProgressIndicator progressIndicator) throws K4Exception, IOException {

        boolean responseMsg = false;
        boolean c = false;
        progressIndicator.setText("Wait for response...");
        synchronized (inputStream) {
            while (!responseMsg) { // throw away incoming aync, and error out on incoming sync
                inputStream.readFully(b = new byte[8]);
                a = b[0] == 1;
                c = b[2] == 1;
                byte msgType = b[1];
                if (msgType == 1) {
                    close();
                    throw new IOException("Cannot process sync msg from remote");
                }
                progressIndicator.setFraction(0);
                responseMsg = msgType == 2;
                j = 4;

                final int msgLength = ri() - 8;
                progressIndicator.setText("Receiving response...");
                try {
                    b = new byte[msgLength];
                    int total = 0;
                    progressIndicator.setText((total / 1024) + " of " + (msgLength / 1024) + " kB");
                    int packetSize = 1 + msgLength / 100;
                    if (packetSize < rxBufferSize)
                        packetSize = rxBufferSize;

                    while (total < msgLength) {
                        progressIndicator.checkCanceled();
                        int remainder = msgLength - total;
                        if (remainder < packetSize)
                            packetSize = remainder;
                        total += inputStream.read(b, total, packetSize);
                        progressIndicator.setFraction(total / msgLength);
                        progressIndicator.setText((total / 1024) + " of " + (msgLength / 1024) + " kB");
                    }
                } finally {
                }
            }
            if (c)
                u();
            else
                j = 0;

            if (b[0] == -128) {
                j = 1;
                throw new K4Exception(rs().toString(true));
            }
            return r();
        }
    }

    private void u() {
        int n = 0, r = 0, f = 0, s = 8, p = s;
        short i = 0;
        j = 0;
        byte[] dst = new byte[ri()];
        int d = j;
        int[] aa = new int[256];
        while (s < dst.length) {
            if (i == 0) {
                f = 0xff & (int) b[d++];
                i = 1;
            }
            if ((f & i) != 0) {
                r = aa[0xff & (int) b[d++]];
                dst[s++] = dst[r++];
                dst[s++] = dst[r++];
                n = 0xff & (int) b[d++];
                for (int m = 0; m < n; m++) {
                    dst[s + m] = dst[r + m];
                }
            } else {
                dst[s++] = b[d++];
            }
            while (p < s - 1) {
                aa[(0xff & (int) dst[p]) ^ (0xff & (int) dst[p + 1])] = p++;
            }
            if ((f & i) != 0) {
                p = s += n;
            }
            i *= 2;
            if (i == 256) {
                i = 0;
            }
        }
        b = dst;
        j = 8;
    }

    public void k(KBase x) throws K4Exception, IOException {
        w(1, x);
    }

    boolean rb() {
        return 1 == b[j++];
    }

    short rh() {
        int x = b[j++], y = b[j++];
        return (short) (a ? x & 0xff | y << 8 : x << 8 | y & 0xff);
    }

    int ri() {
        int x = rh(), y = rh();
        return a ? x & 0xffff | y << 16 : x << 16 | y & 0xffff;
    }

    long rj() {
        int x = ri(), y = ri();
        return a ? x & 0xffffffffL | (long) y << 32 : (long) x << 32 | y & 0xffffffffL;
    }

    float re() {
        return Float.intBitsToFloat(ri());
    }

    double rf() {
        return Double.longBitsToDouble(rj());
    }

    UUID rg() {
        boolean oa = a;
        a = false;
        UUID g = new UUID(rj(), rj());
        a = oa;
        return g;
    }

    char rc() {
        return (char) (b[j++] & 0xff);
    }

    KSymbol rs() {
        int n = j;
        for (; b[n] != 0; )
            ++n;
        String s = null;
        try {
            s = new String(b, j, n - j, Config.getInstance().getEncoding());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.WARNING, null, ex);
        }
        j = n;
        ++j;
        return new KSymbol(s);
    }

    UnaryPrimitive rup() {
        return new UnaryPrimitive(b[j++]);
    }

    BinaryPrimitive rbp() {
        return new BinaryPrimitive(b[j++]);
    }

    TernaryOperator rternary() {
        return new TernaryOperator(b[j++]);
    }

    Function rfn() {
        KSymbol s = rs();
        return new Function((KCharacterVector) r());
    }

    Feach rfeach() {
        return new Feach(r());
    }

    Fover rfover() {
        return new Fover(r());
    }

    Fscan rfscan() {
        return new Fscan(r());
    }

    FComposition rcomposition() {
        int n = ri();
        Object[] objs = new Object[n];
        for (int i = 0; i < n; i++)
            objs[i] = r();

        return new FComposition(objs);
    }

    FPrior rfPrior() {
        return new FPrior(r());
    }

    FEachRight rfEachRight() {
        return new FEachRight(r());
    }

    FEachLeft rfEachLeft() {
        return new FEachLeft(r());
    }

    Projection rproj() {
        int n = ri();
        KList list = new KList(n);
        KBase[] array = (KBase[]) list.getArray();
        for (int i = 0; i < n; i++)
            array[i] = r();

        return new Projection(list);
    }

    Minute ru() {
        return new Minute(ri());
    }

    Month rm() {
        return new Month(ri());
    }

    Second rv() {
        return new Second(ri());
    }

    KTimespan rn() {
        return new KTimespan(rj());
    }

    KTime rt() {
        return new KTime(ri());
    }

    KDate rd() {
        return new KDate(ri());
    }

    KDatetime rz() {
        return new KDatetime(rf());
    }

    KTimestamp rp() {
        return new KTimestamp(rj());
    }

    KBase r() {
        int i = 0, n, t = b[j++];
        if (t < 0)
            switch (t) {
                case -1:
                    return new KBoolean(rb());
                case -2:
                    return new KGuid(rg());
                case -4:
                    return new KByte(b[j++]);
                case -5:
                    return new KShort(rh());
                case -6:
                    return new KInteger(ri());
                case -7:
                    return new KLong(rj());
                case -8:
                    return new KFloat(re());
                case -9:
                    return new KDouble(rf());
                case -10:
                    return new KCharacter(rc());
                case -11:
                    return rs();
                case -12:
                    return rp();
                case -13:
                    return rm();
                case -14:
                    return rd();
                case -15:
                    return rz();
                case -16:
                    return rn();
                case -17:
                    return ru();
                case -18:
                    return rv();
                case -19:
                    return rt();
            }

        if (t == 100)
            return rfn(); // fn - lambda
        if (t == 101)
            return rup();  // unary primitive
        if (t == 102)
            return rbp();  // binary primitive
        if (t == 103)
            return rternary();
        if (t == 104)
            return rproj(); // fn projection
        if (t == 105)
            return rcomposition();

        if (t == 106)
            return rfeach(); // f'
        if (t == 107)
            return rfover(); // f/
        if (t == 108)
            return rfscan(); //f\
        if (t == 109)
            return rfPrior(); // f':
        if (t == 110)
            return rfEachRight(); // f/:
        if (t == 111)
            return rfEachLeft(); // f\:
        if (t == 112) {
            // dynamic load
            j++;
            return null;
        }
        if (t == 127) {
            Dict d = new Dict(r(), r());
            d.setAttr((byte) 1);
            return d;
        }
        if (t > 99) {
            j++;
            return null;
        }
        if (t == 99)
            return new Dict(r(), r());
        byte attr = b[j++];
        if (t == 98)
            return new Flip((Dict) r());
        n = ri();
        switch (t) {
            case 0: {
                KList L = new KList(n);
                L.setAttr(attr);
                KBase[] array = (KBase[]) L.getArray();
                for (; i < n; i++)
                    array[i] = r();
                return L;
            }
            case 1: {
                KBooleanVector B = new KBooleanVector(n);
                B.setAttr(attr);
                boolean[] array = (boolean[]) B.getArray();
                for (; i < n; i++)
                    array[i] = rb();
                return B;
            }
            case 2: {
                KGuidVector B = new KGuidVector(n);
                B.setAttr(attr);
                UUID[] array = (UUID[]) B.getArray();
                for (; i < n; i++)
                    array[i] = rg();
                return B;
            }
            case 4: {
                KByteVector G = new KByteVector(n);
                G.setAttr(attr);
                byte[] array = (byte[]) G.getArray();
                for (; i < n; i++)
                    array[i] = b[j++];
                return G;
            }
            case 5: {
                KShortVector H = new KShortVector(n);
                H.setAttr(attr);
                short[] array = (short[]) H.getArray();
                for (; i < n; i++)
                    array[i] = rh();
                return H;
            }
            case 6: {
                KIntVector I = new KIntVector(n);
                I.setAttr(attr);
                int[] array = (int[]) I.getArray();
                for (; i < n; i++)
                    array[i] = ri();
                return I;
            }
            case 7: {
                KLongVector J = new KLongVector(n);
                J.setAttr(attr);
                long[] array = (long[]) J.getArray();
                for (; i < n; i++)
                    array[i] = rj();
                return J;
            }
            case 8: {
                KFloatVector E = new KFloatVector(n);
                E.setAttr(attr);
                float[] array = (float[]) E.getArray();
                for (; i < n; i++)
                    array[i] = re();
                return E;
            }
            case 9: {
                KDoubleVector F = new KDoubleVector(n);
                F.setAttr(attr);
                double[] array = (double[]) F.getArray();
                for (; i < n; i++)
                    array[i] = rf();
                return F;
            }
            case 10: {
                KCharacterVector C = null;
                try {
                    char[] array = new String(b, j, n, Config.getInstance().getEncoding()).toCharArray();
                    C = new KCharacterVector(array);
                    C.setAttr(attr);
                } catch (UnsupportedEncodingException e) {
                    Logger.getLogger(Connector.class.getName()).log(Level.WARNING, null, e);
                }
                j += n;
                return C;
            }
            case 11: {
                KSymbolVector S = new KSymbolVector(n);
                S.setAttr(attr);
                String[] array = (String[]) S.getArray();
                for (; i < n; i++)
                    array[i] = rs().s;
                return S;
            }
            case 12: {
                KTimestampVector P = new KTimestampVector(n);
                P.setAttr(attr);
                long[] array = (long[]) P.getArray();
                for (; i < n; i++) {
                    array[i] = rj();
                }
                return P;
            }
            case 13: {
                KMonthVector M = new KMonthVector(n);
                M.setAttr(attr);
                int[] array = (int[]) M.getArray();
                for (; i < n; i++)
                    array[i] = ri();
                return M;
            }
            case 14: {
                KDateVector D = new KDateVector(n);
                D.setAttr(attr);
                int[] array = (int[]) D.getArray();
                for (; i < n; i++)
                    array[i] = ri();
                return D;
            }
            case 15: {
                KDatetimeVector Z = new KDatetimeVector(n);
                Z.setAttr(attr);
                double[] array = (double[]) Z.getArray();
                for (; i < n; i++)
                    array[i] = rf();
                return Z;
            }
            case 16: {
                KTimespanVector N = new KTimespanVector(n);
                N.setAttr(attr);
                long[] array = (long[]) N.getArray();
                for (; i < n; i++) {
                    array[i] = rj();
                }
                return N;
            }
            case 17: {
                KMinuteVector U = new KMinuteVector(n);
                U.setAttr(attr);
                int[] array = (int[]) U.getArray();
                for (; i < n; i++)
                    array[i] = ri();
                return U;
            }
            case 18: {
                KSecondVector V = new KSecondVector(n);
                V.setAttr(attr);
                int[] array = (int[]) V.getArray();
                for (; i < n; i++)
                    array[i] = ri();
                return V;
            }
            case 19: {
                KTimeVector T = new KTimeVector(n);
                T.setAttr(attr);
                int[] array = (int[]) T.getArray();
                for (; i < n; i++)
                    array[i] = ri();
                return T;
            }
        }
        return null;
    }

}
