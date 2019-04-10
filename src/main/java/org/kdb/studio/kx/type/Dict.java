package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class Dict extends KBase {
    public String getDataType() {
        return "Dictionary";
    }

    public KBase x;
    public KBase y;

    public Dict(KBase X, KBase Y) {
        type = 99;
        x = X;
        y = Y;
    }

    public void upsert(Dict upd) {
        //if dict is not table
        if (!(x instanceof Flip) || !(y instanceof Flip))
            return;
        //if upd is not table
        if (!(upd.x instanceof Flip) || !(upd.y instanceof Flip))
            return;
        Flip cx = (Flip) x;
        Flip cy = (Flip) y;
        Flip updx = (Flip) upd.x;
        Flip updy = (Flip) upd.y;
        cx.append(updx);
        cy.append(updy);
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        boolean useBrackets = getAttr() != 0 || x instanceof Flip;
        super.toString(w, showType);
        if (useBrackets)
            w.write("(");
        x.toString(w, showType);
        if (useBrackets)
            w.write(")");
        w.write("!");
        y.toString(w, showType);
    }
}