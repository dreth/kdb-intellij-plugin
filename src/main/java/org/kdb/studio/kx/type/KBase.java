package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;

public abstract class KBase {

    static long MILLIS_IN_DAY = 86400000L;

    static int DAYS_BETWEEN_1970_AND_2000 = 10957;

    static java.text.DecimalFormat i2Formatter= new java.text.DecimalFormat("00");

    public abstract String getDataType();

    public int type;

    public void serialise(OutputStream o) throws IOException {
        Writer.write(o, (byte) type);
    }

    public String toString() {
        return toString(true);
    }

    public boolean isNull() {
        return false;
    }

    private byte attr;

    public byte getAttr() {
        return attr;
    }

    public void setAttr(byte attr) {
        this.attr = attr;
    }

    private static String[] sAttr = new String[]{"", "`s#", "`u#", "`p#", "`g#"};

    public String toString(boolean showType) {
        if (attr <= sAttr.length)
            return sAttr[attr];
        return "";
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    protected String i2(int i) {
        return i2Formatter.format(i);
    }

}