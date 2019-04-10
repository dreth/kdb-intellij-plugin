package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KTimespanVector extends KBaseVector {
    public String getDataType() {
        return "Timespan Vector";
    }

    public KTimespanVector(int length) {
        super(long.class, length);
        type = 16;
    }

    public KBase at(int i) {
        return new KTimespan(Array.getLong(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`timespan$()");
        else {
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                w.write(at(i).toString(false));
            }
        }
    }
}