package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.ToDouble;

import java.io.IOException;

public class KInteger extends KBase implements ToDouble {
    public String getDataType() {
        return "Integer";
    }

    public int i;

    public double toDouble() {
        return i;
    }

    public KInteger(int i) {
        this.i = i;
        type = -6;
    }

    public boolean isNull() {
        return i == Integer.MIN_VALUE;
    }

    public String toString(boolean showType) {
        String s;
        if (isNull())
            s = "0N";
        else if (i == Integer.MAX_VALUE)
            s = "0W";
        else if (i == -Integer.MAX_VALUE)
            s = "-0W";
        else
            s = Integer.toString(i);
        if (showType)
            s += "i";
        return s;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }
}