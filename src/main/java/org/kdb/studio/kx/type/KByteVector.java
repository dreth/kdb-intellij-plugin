package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;

public class KByteVector extends KBaseVector {
    public String getDataType() {
        return "Byte Vector";
    }

    public KByteVector(int length) {
        super(byte.class, length);
        type = 4;
    }

    public KBase at(int i) {
        return new KByte(Array.getByte(array, i));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        Writer.write(o, (byte) 0);
        Writer.write(o, getLength());
        for (int i = 0; i < getLength(); i++) {
            Writer.write(o, Array.getByte(array, i));
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));
        if (getLength() == 0)
            w.write("`byte$()");
        else {
            if (getLength() == 1)
                w.write(enlist);

            w.write("0x");
            for (int i = 0; i < getLength(); i++) {
                byte b = Array.getByte(array, i);
                w.write(Integer.toHexString((b >> 4) & 0xf) + Integer.toHexString(b & 0xf));
            }
        }
    }
}