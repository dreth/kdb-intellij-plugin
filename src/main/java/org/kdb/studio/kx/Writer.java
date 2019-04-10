package org.kdb.studio.kx;

import java.io.IOException;
import java.io.OutputStream;

public class Writer {

    public static void write(OutputStream o, byte b) throws IOException {
        o.write(b);
    }

    public static void write(OutputStream o, short h) throws IOException {
        write(o, (byte) (h >> 8));
        write(o, (byte) h);
    }

    public static void write(OutputStream o, int i) throws IOException {
        write(o, (short) (i >> 16));
        write(o, (short) i);
    }

    public static void write(OutputStream o, long j) throws IOException {
        write(o, (int) (j >> 32));
        write(o, (int) j);
    }

}
