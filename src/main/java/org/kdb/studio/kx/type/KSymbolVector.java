package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public class KSymbolVector extends KBaseVector {
    public String getDataType() {
        return "Symbol Vector";
    }

    public KSymbolVector(int length) {
        super(String.class, length);
        type = 11;
    }

    public KBase at(int i) {
        return new KSymbol((String) Array.get(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));
        if (getLength() == 0)
            w.write("0#`");
        else if (getLength() == 1)
            w.write(enlist);

        for (int i = 0; i < getLength(); i++)
            w.write("`" + (String) Array.get(array, i));
    }
}