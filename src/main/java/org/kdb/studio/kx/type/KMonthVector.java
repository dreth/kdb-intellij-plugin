package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KMonthVector extends KBaseVector {
    public String getDataType() {
        return "Month Vector";
    }

    public KMonthVector(int length) {
        super(int.class, length);
        type = 13;
    }

    public KBase at(int i) {
        return new Month(Array.getInt(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`month$()");
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
                else {
                    int m = v + 24000, y = m / 12;
                    String s = i2(y / 100) + i2(y % 100) + "." + i2(1 + m % 12);
                    w.write(s);
                }
            }
            if (showType)
                w.write("m");
        }
    }
}