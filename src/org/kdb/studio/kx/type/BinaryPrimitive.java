package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class BinaryPrimitive extends Primitive {
    private static String[] ops = {":", "+", "-", "*", "%", "&", "|", "^", "=", "<", ">", "$", ",", "#", "_", "~", "!", "?", "@", ".", "0:", "1:", "2:", "in", "within", "like", "bin", "ss", "insert", "wsum", "wavg", "div", "xexp", "setenv"};

    public String getDataType() {
        return "Binary Primitive";
    }

    public BinaryPrimitive(int i) {
        super(ops, i);
        type = 102;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(getPrimitive());
    }
}