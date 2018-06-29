package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.Writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.Time;

public class KTimeVector extends KBaseVector {
    public String getDataType() {
        return "Time Vector";
    }

    public KTimeVector(int length) {
        super(int.class, length);
        type = 19;
    }

    public KBase at(int i) {
        return new KTime(Array.getInt(array, i));
    }

    public void serialise(OutputStream o) throws IOException {
        super.serialise(o);
        Writer.write(o, (byte) 0);
        Writer.write(o, getLength());
        for (int i = 0; i < getLength(); i++) {
            Writer.write(o, Array.getInt(array, i));
        }
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`time$()");
        else {
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                int v = Array.getInt(array, i);
                if (v == Integer.MIN_VALUE)
                    w.write("0Nt");
                else if (v == Integer.MAX_VALUE)
                    w.write("0Wt");
                else if (v == -Integer.MAX_VALUE)
                    w.write("-0Wt");
                else
                    w.write(Config.getInstance().getDateFormat("HH:mm:ss.SSS").format(new Time(v)));
            }
        }
    }
}