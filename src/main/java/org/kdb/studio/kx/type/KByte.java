package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.ToDouble;

import java.io.IOException;

public class KByte extends KBase implements ToDouble {
    public String getDataType() {
        return "Byte";
    }

    public byte b;

    public double toDouble() {
        return b;
    }

    public KByte(byte b) {
        this.b = b;
        type = -4;
    }

    public String toString(boolean showType) {
        return "0x" + Integer.toHexString((b >> 4) & 0xf) + Integer.toHexString(b & 0xf);
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }
}