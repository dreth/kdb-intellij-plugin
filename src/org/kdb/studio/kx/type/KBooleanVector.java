package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;

public class KBooleanVector extends KBaseVector {
    public String getDataType() {
        return "Boolean Vector";
    }

    public KBooleanVector(int length) {
        super(boolean.class, length);
        type = 1;
    }

    public KBase at(int i) {
        return new KBoolean(Array.getBoolean(array, i));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        Writer.write(o, (byte) 0);
        Writer.write(o, getLength());
        for (int i = 0; i < getLength(); i++) {
            Writer.write(o, (byte) (Array.getBoolean(array, i) ? 1 : 0));
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));
        if (getLength() == 0)
            w.write("`boolean$()");
        else {
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++)
                w.write((Array.getBoolean(array, i) ? "1" : "0"));
            w.write("b");
        }
    }
}