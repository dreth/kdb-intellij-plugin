package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class FEachLeft extends Adverb {
    public FEachLeft(KBase o) {
        super(o);
        type = 111;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        o.toString(w, showType);
        w.write("\\:");
    }
}