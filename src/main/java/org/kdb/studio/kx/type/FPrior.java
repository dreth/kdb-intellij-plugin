package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class FPrior extends Adverb {
    public FPrior(KBase o) {
        super(o);
        type = 109;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        o.toString(w, showType);
        w.write("':");
    }
}