package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.ToDouble;

import java.io.IOException;

public class KShort extends KBase implements ToDouble {
    public String getDataType() {
        return "Short";
    }

    public short s;

    public double toDouble() {
        return s;
    }

    public KShort(short s) {
        this.s = s;
        type = -5;
    }

    public boolean isNull() {
        return s == Short.MIN_VALUE;
    }

    public String toString(boolean showType) {
        String t;
        if (s == Short.MIN_VALUE)
            t = "0N";
        else if (s == Short.MAX_VALUE)
            t = "0W";
        else if (s == -Short.MAX_VALUE)
            t = "-0W";
        else
            t = Short.toString(s);
        if (showType)
            t += "h";
        return t;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }
}