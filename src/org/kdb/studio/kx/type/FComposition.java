package org.kdb.studio.kx.type;

public class FComposition extends KBase {
    Object[] objs;

    public String getDataType() {
        return "Function Composition";
    }

    public FComposition(Object[] objs) {
        this.objs = objs;
        type = 105;
    }

    public Object[] getObjs() {
        return objs;
    }
}