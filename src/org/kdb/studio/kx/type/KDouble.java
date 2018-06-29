package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.ToDouble;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;

public class KDouble extends KBase implements ToDouble {
    public String getDataType() {
        return "Double";
    }

    public double d;

    public KDouble(double d) {
        type = -9;
        this.d = d;
    }

    public double toDouble() {
        return d;
    }

    public boolean isNull() {
        return Double.isNaN(d);
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0n";
        else if (d == Double.POSITIVE_INFINITY)
            return "0w";
        else if (d == Double.NEGATIVE_INFINITY)
            return "-0w";
        else {
            String s = Config.getInstance().getNumberFormat().format(d);
            if (showType) {
                double epsilon = 1e-9;
                double diff = d - Math.round(d);
                if ((diff < epsilon) && (diff > -epsilon))
                    s += "f";
            }
            return s;
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        long j = Double.doubleToLongBits(d);
        Writer.write(o, j);
    }
}