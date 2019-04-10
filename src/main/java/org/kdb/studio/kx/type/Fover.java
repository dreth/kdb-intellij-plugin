package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class Fover extends Adverb {
    public Fover(KBase o) {
        super(o);
        type = 107;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        o.toString(w, showType);
        w.write("/");
    }
}