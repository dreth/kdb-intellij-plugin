package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class UnaryPrimitive extends Primitive {
    private static String[] ops = {"::", "+:", "-:", "*:", "%:", "&:", "|:", "^:", "=:", "<:", ">:", "$:", ",:", "#:", "_:", "~:", "!:", "?:", "@:", ".:", "0::", "1::", "2::", "avg", "last", "sum", "prd", "min", "max", "exit", "getenv", "abs", "sqrt", "log", "exp", "sin", "asin", "cos", "acos", "tan", "atan", "enlist"};

    public UnaryPrimitive(int i) {
        super(ops, i);
        type = 101;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        if (getPrimitiveAsInt() == -1)
            return;
        w.write(getPrimitive());
    }
}