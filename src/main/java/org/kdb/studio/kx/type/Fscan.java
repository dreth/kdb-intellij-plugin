package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class Fscan extends Adverb {
    public Fscan(KBase o) {
        super(o);
        type = 108;
        this.o = o;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        o.toString(w, showType);
        w.write("\\");
    }
}