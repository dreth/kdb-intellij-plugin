package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.sql.Time;

public class KTimespan extends KBase {
    public long j;

    public KTimespan(long x) {
        j = x;
        type = -16;
    }

    public String getDataType() {
        return "Timespan";
    }

    public boolean isNull() {
        return j == Long.MIN_VALUE;
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0Nn";
        else if (j == Long.MAX_VALUE)
            return "0Wn";
        else if (j == -Long.MAX_VALUE)
            return "-0Wn";
        else {
            String s = "";
            long jj = j;
            if (jj < 0) {
                jj = -jj;
                s = "-";
            }
            int d = ((int) (jj / 86400000000000L));
            if (d != 0)
                s += d + "D";
            return s + i2((int) ((jj % 86400000000000L) / 3600000000000L)) +
                    ":" + i2((int) ((jj % 3600000000000L) / 60000000000L)) +
                    ":" + i2((int) ((jj % 60000000000L) / 1000000000L)) +
                    "." + Config.getInstance().getNanosFormat().format((int) (jj % 1000000000L));
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Time toTime() {
        return new Time((j / 1000000));
    }
}