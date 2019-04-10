package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class Second extends KBase {
    public String getDataType() {
        return "Second";
    }

    public int i;

    public Second(int x) {
        type = -18;
        i = x;
    }

    public boolean isNull() {
        return i == Integer.MIN_VALUE;
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0Nv";
        else if (i == Integer.MAX_VALUE)
            return "0Wv";
        else if (i == -Integer.MAX_VALUE)
            return "-0Wv";
        else
            return new Minute(i / 60).toString() + ':' + i2(i % 60);
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Instant toDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, i / (60 * 60));
        cal.set(Calendar.MINUTE, (int) ((i % (60 * 60)) / 60));
        cal.set(Calendar.SECOND, i % 60);
        return cal.toInstant();
    }
}