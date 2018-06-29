package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class Flip extends KBase {
    protected static final String flip = "flip ";
    public String getDataType() {
        return "Flip";
    }

    public KSymbolVector x;
    public KBaseVector y;

    public Flip(Dict X) {
        type = 98;
        x = (KSymbolVector) X.x;
        y = (KBaseVector) X.y;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        boolean usebracket = x.getLength() == 1;
        w.write(flip);
        if (usebracket)
            w.write("(");
        x.toString(w, showType);
        if (usebracket)
            w.write(")");
        w.write("!");
        y.toString(w, showType);
    }

    public void append(Flip nf) {
        for (int i = 0; i < y.getLength(); i++)
            ((KBaseVector) y.at(i)).append((KBaseVector) nf.y.at(i));
    }
}