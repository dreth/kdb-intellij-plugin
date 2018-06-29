package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TernaryOperator extends KBase {
    public String getDataType() {
        return "Ternary Operator";
    }

    private static Map<Integer, Character> map = new HashMap<>();

    public static void init(char[] ops, int[] values) {
        for (int i = 0; i < values.length; i++)
            map.put(values[i], ops[i]);
    }

    private int primitive;
    private char charVal = ' ';


    static {
        init("'/\\".toCharArray(), new int[]{0, 1, 2});
    }

    public TernaryOperator(int i) {
        type = 103;
        primitive = i;
        Character c = (Character) map.get(new Integer(i));
        if (c != null)
            charVal = c.charValue();
    }

    public char getPrimitive() {
        return charVal;
    }

    public int getPrimitiveAsInt() {
        return primitive;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(charVal);
    }
}