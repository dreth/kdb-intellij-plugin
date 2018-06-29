package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KLongVector extends KBaseVector {
    public String getDataType() {
        return "Long Vector";
    }

    public KLongVector(int length) {
        super(long.class, length);
        type = 7;
    }

    public KBase at(int i) {
        return new KLong(Array.getLong(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`long$()");
        else {
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                long v = Array.getLong(array, i);
                if (v == Long.MIN_VALUE)
                    w.write("0N");
                else if (v == Long.MAX_VALUE)
                    w.write("0W");
                else if (v == -Long.MAX_VALUE)
                    w.write("-0W");
                else {
                    w.write("" + v);
                }
            }
            if (showType)
                w.write("j");
        }
    }
}