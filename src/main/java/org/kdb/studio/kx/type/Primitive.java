package org.kdb.studio.kx.type;

public class Primitive extends KBase {
    public String getDataType() {
        return "Primitive";
    }

    ;
    private int primitive;
    private String s = " ";

    public Primitive(String[] ops, int i) {
        primitive = i;
        if (i >= 0 && i < ops.length)
            s = ops[i];
    }

    public String getPrimitive() {
        return s;
    }

    public int getPrimitiveAsInt() {
        return primitive;
    }
}