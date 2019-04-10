package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class Month extends KBase {

    public String getDataType() {
        return "Month";
    }

    public int i;

    public Month(int x) {
        type = -13;
        i = x;
    }

    public boolean isNull() {
        return i == Integer.MIN_VALUE;
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0Nm";
        else if (i == Integer.MAX_VALUE)
            return "0Wm";
        else if (i == -Integer.MAX_VALUE)
            return "-0Wm";
        else {
            int m = i + 24000, y = m / 12;
            String s = i2(y / 100) + i2(y % 100) + "." + i2(1 + m % 12);
            if (showType)
                s += "m";
            return s;
        }
    }

    public Instant toDate() {
        int m = i + 24000, y = m / 12;
        Calendar cal = Calendar.getInstance();
        cal.set(y, m, 01);
        return cal.toInstant();
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }
}