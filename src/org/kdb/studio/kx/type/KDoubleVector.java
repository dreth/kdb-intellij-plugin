package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.NumberFormat;

public class KDoubleVector extends KBaseVector {
    public String getDataType() {
        return "Double Vector";
    }

    public KDoubleVector(int length) {
        super(double.class, length);
        type = 9;
    }

    public KBase at(int i) {
        return new KDouble(Array.getDouble(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`float$()");
        else {
            if (getLength() == 1)
                w.write(enlist);

            boolean printedP = false;
            NumberFormat nf = Config.getInstance().getNumberFormat();
            for (int i = 0; i < getLength(); i++) {
                double d = Array.getDouble(array, i);
                if (i > 0)
                    w.write(" ");
                if (Double.isNaN(d)) {
                    w.write("0n");
                    printedP = true;
                } else if (d == Double.POSITIVE_INFINITY) {
                    w.write("0w");
                    printedP = true;
                } else if (d == Double.NEGATIVE_INFINITY) {
                    w.write("-0w");
                    printedP = true;
                } else {
                    double epsilon = 1e-9;
                    double diff = d - Math.round(d);
                    if (!((diff < epsilon) && (diff > -epsilon)))
                        printedP = true;
                    w.write(nf.format(d));
                }
            }
            if (!printedP)
                w.write("f");
        }
    }
}