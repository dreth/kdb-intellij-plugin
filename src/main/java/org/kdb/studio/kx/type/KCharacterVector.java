package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;

public class KCharacterVector extends KBaseVector {
    public String getDataType() {
        return "Character Vector";
    }


    public KCharacterVector(int length) {
        super(char.class, length);
        type = 10;
    }

    public KCharacterVector(char[] ca) {
        super(char.class, ca.length);
        System.arraycopy(ca, 0, array, 0, ca.length);
        type = 10;
    }

    public KCharacterVector(String s) {
        super(char.class, s.toCharArray().length);
        System.arraycopy(s.toCharArray(), 0, array, 0, s.toCharArray().length);
        type = 10;
    }

    public KBase at(int i) {
        return new KCharacter(Array.getChar(array, i));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        byte[] b = new String((char[]) array).getBytes(Config.getInstance().getEncoding());
        Writer.write(o, (byte) 0);
        Writer.write(o, b.length);
        o.write(b);
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));
        if (getLength() == 1)
            w.write(enlist);

        if (showType)
            w.write("\"");
        for (int i = 0; i < getLength(); i++)
            w.write(Array.getChar(array, i));
        if (showType)
            w.write("\"");
    }

    public String toString(boolean showType) {
        try {
            LimitedWriter lw = new LimitedWriter(256);
            toString(lw, showType);
            return lw.toString();
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder(256);
            if (getLength() == 1)
                sb.append(enlist);
            sb.append('"').append((char[]) array).append('"');
            return sb.toString();
        }
    }
}