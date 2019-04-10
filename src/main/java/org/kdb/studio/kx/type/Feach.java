package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class Feach extends Adverb {
    public Feach(KBase o) {
        super(o);
        type = 106;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        o.toString(w, showType);
        w.write("'");
    }
}