package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KList extends KBaseVector {
    public String getDataType() {
        return "List";
    }

    public KList(int length) {
        super(KBase.class, length);
        type = 0;
    }

    public KBase at(int i) {
        return (KBase) Array.get(array, i);
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 1)
            w.write(enlist);
        else
            w.write("(");
        for (int i = 0; i < getLength(); i++) {
            if (i > 0)
                w.write(";");
            at(i).toString(w, showType);
        }
        if (getLength() != 1)
            w.write(")");
    }
}