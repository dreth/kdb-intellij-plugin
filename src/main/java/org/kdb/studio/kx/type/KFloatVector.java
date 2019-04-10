package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.NumberFormat;

public class KFloatVector extends KBaseVector {
    public String getDataType() {
        return "Float Vector";
    }

    public KFloatVector(int length) {
        super(float.class, length);
        type = 8;
    }

    public KBase at(int i) {
        return new KFloat(Array.getFloat(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`real$()");
        else {
            if (getLength() == 1)
                w.write(enlist);

            boolean printedP = false;
            NumberFormat nf = Config.getInstance().getNumberFormat();
            for (int i = 0; i < getLength(); i++) {
                float d = Array.getFloat(array, i);
                if (i > 0)
                    w.write(" ");
                if (Float.isNaN(d)) {
                    w.write("0N");
                    printedP = true;
                } else if (d == Float.POSITIVE_INFINITY) {
                    w.write("0W");
                    printedP = true;
                } else if (d == Float.NEGATIVE_INFINITY) {
                    w.write("-0W");
                    printedP = true;
                } else {
                    if (d != ((int) d))
                        printedP = true;
                    w.write(nf.format(d));
                }
            }
            if (!printedP)
                w.write("e");
        }
    }
}