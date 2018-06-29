package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.sql.Timestamp;

public class KTimestamp extends KBase {
    public String getDataType() {
        return "Timestamp";
    }

    long time;

    public KTimestamp(long time) {
        type = -12;
        this.time = time;
    }

    public boolean isNull() {
        return time == Long.MIN_VALUE;
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0Np";
        else if (time == Long.MAX_VALUE)
            return "0Wp";
        else if (time == -Long.MAX_VALUE)
            return "-0Wp";
        else {
            Timestamp ts = toTimestamp();
            return Config.getInstance().getDateFormat("yyyy.MM.dd HH:mm:ss.").format(ts) + Config.getInstance().getNanosFormat().format(ts.getNanos());
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Timestamp toTimestamp() {
        long k = 86400000L * 10957;
        long n = 1000000000L;
        long d = time < 0 ? (time + 1) / n - 1 : time / n;
        long ltime = time == Long.MIN_VALUE ? time : (k + 1000 * d);
        int nanos = (int) (time - n * d);
        Timestamp ts = new Timestamp(ltime);
        ts.setNanos(nanos);
        return ts;
    }
}