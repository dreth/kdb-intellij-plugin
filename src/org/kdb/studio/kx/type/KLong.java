package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.ToDouble;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;

public class KLong extends KBase implements ToDouble {
    public String getDataType() {
        return "Long";
    }

    ;
    public long j;

    public double toDouble() {
        return j;
    }

    public KLong(long j) {
        this.j = j;
        type = -7;
    }

    public boolean isNull() {
        return j == Long.MIN_VALUE;
    }

    public String toString(boolean showType) {
        String s;
        if (isNull())
            s = "0N";
        else if (j == Long.MAX_VALUE)
            s = "0W";
        else if (j == -Long.MAX_VALUE)
            s = "-0W";
        else {
            s = Long.toString(j);
        }
        if (showType)
            s += "j";

        return s;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        Writer.write(o, j);
    }
}