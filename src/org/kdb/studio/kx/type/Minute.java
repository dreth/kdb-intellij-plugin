package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class Minute extends KBase {
    public String getDataType() {
        return "Minute";
    }

    public int i;

    public Minute(int x) {
        type = -17;
        i = x;
    }

    public boolean isNull() {
        return i == Integer.MIN_VALUE;
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0Nu";
        else if (i == Integer.MAX_VALUE)
            return "0Wu";
        else if (i == -Integer.MAX_VALUE)
            return "-0Wu";
        else
            return i2(i / 60) + ":" + i2(i % 60);
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Instant toDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, i / 60);
        cal.set(Calendar.MINUTE, i % 60);
        return cal.toInstant();
    }
}