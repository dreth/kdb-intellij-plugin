package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KTimestampVector extends KBaseVector {
    public String getDataType() {
        return "Timestamp Vector";
    }

    public KTimestampVector(int length) {
        super(long.class, length);
        type = 12;
    }

    public KBase at(int i) {
        return new KTimestamp(Array.getLong(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`timestamp$()");
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