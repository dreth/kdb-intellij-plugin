package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

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
            Object[] ts = toTimestamp();
            return Config.getInstance().getDateTimeFormatter("yyyy.MM.dd HH:mm:ss.").format(Instant.class.cast(ts[0])) + Config.getInstance().getNanosFormat().format(ts[1]);
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Object[] toTimestamp() {
        long k = MILLIS_IN_DAY * DAYS_BETWEEN_1970_AND_2000;
        long n = 1000000000L;
        long d = time < 0 ? (time + 1) / n - 1 : time / n;
        long ltime = time == Long.MIN_VALUE ? time : (k + 1000 * d);
        int nanos = (int) (time - n * d);
        return new Object[] {Instant.ofEpochMilli(ltime), nanos};
    }
}