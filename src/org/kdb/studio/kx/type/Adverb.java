package org.kdb.studio.kx.type;

public class Adverb extends KBase {
    public String getDataType() {
        return "Adverb";
    }

    protected KBase o;

    public Adverb(KBase o) {
        this.o = o;
    }

    public Object getObject() {
        return o;
    }
}