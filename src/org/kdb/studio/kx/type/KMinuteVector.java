package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KMinuteVector extends KBaseVector {
    public String getDataType() {
        return "Minute Vector";
    }


    public KMinuteVector(int length) {
        super(int.class, length);
        type = 17;
    }

    public KBase at(int i) {
        return new Minute(Array.getInt(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`minute$()");
        else {
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                int v = Array.getInt(array, i);
                if (v == Integer.MIN_VALUE)
                    w.write("0Nu");
                else if (v == Integer.MAX_VALUE)
                    w.write("0Wu");
                else if (v == -Integer.MAX_VALUE)
                    w.write("-0Wu");
                else
                    w.write(i2(v / 60) + ":" + i2(v % 60));
            }
        }
    }
}