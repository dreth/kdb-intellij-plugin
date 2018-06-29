package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.ToDouble;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;

public class KFloat extends KBase implements ToDouble {
    public String getDataType() {
        return "Float";
    }

    ;
    public float f;

    public double toDouble() {
        return f;
    }

    public KFloat(float f) {
        type = -8;
        this.f = f;
    }

    public boolean isNull() {
        return Float.isNaN(f);
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0ne";
        else if (f == Float.POSITIVE_INFINITY)
            return "0we";
        else if (f == Float.NEGATIVE_INFINITY)
            return "-0we";
        else {
            String s = Config.getInstance().getNumberFormat().format(f);
            if (showType) {
                double epsilon = 1e-9;
                double diff = f - Math.round(f);
                if ((diff < epsilon) && (diff > -epsilon))
                    s += "e";
            }
            return s;
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        int i = Float.floatToIntBits(f);
        Writer.write(o, i);
    }
}