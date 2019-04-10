package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class FEachRight extends Adverb {
    public FEachRight(KBase o) {
        super(o);
        type = 110;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        o.toString(w, showType);
        w.write("/:");
    }
}