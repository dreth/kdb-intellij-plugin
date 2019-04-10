package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KIntVector extends KBaseVector {
    public String getDataType() {
        return "Int Vector";
    }

    public KIntVector(int length) {
        super(int.class, length);
        type = 6;
    }

    public KBase at(int i) {
        return new KInteger(Array.getInt(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`int$()");
        else {
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                int v = Array.getInt(array, i);
                if (v == Integer.MIN_VALUE)
                    w.write("0N");
                else if (v == Integer.MAX_VALUE)
                    w.write("0W");
                else if (v == -Integer.MAX_VALUE)
                    w.write("-0W");
                else
                    w.write("" + v);
            }
            if (showType)
                w.write("i");
        }
    }
}