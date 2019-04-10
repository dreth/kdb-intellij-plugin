package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;

public class KCharacter extends KBase {
    public String getDataType() {
        return "Character";
    }

    public char c;

    public KCharacter(char c) {
        this.c = c;
        type = -10;
    }

    public boolean isNull() {
        return c == ' ';
    }

    public String toString(boolean showType) {
        if (showType)
            return "\"" + c + "\"";
        else
            return "" + c;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        Writer.write(o, (byte) c);
    }
}