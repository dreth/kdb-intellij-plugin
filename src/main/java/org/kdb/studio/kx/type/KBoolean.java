package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.ToDouble;

import java.io.IOException;

public class KBoolean extends KBase implements ToDouble {
    public String getDataType() {
        return "Boolean";
    }

    ;
    public boolean b;

    public KBoolean(boolean b) {
        this.b = b;
        type = -1;
    }

    public String toString(boolean showType) {
        String s = b ? "1" : "0";
        if (showType)
            s += "b";
        return s;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public double toDouble() {
        return b ? 1.0 : 0.0;
    }

    public boolean toBoolean() {
        return b;
    }
}