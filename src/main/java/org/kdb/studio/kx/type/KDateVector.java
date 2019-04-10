package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.Date;

public class KDateVector extends KBaseVector {
    public String getDataType() {
        return "Date Vector";
    }

    public KDateVector(int length) {
        super(int.class, length);
        type = 14;
    }

    public KBase at(int i) {
        return new KDate(Array.getInt(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`date$()");
        else {
            boolean printD = true;
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                int v = Array.getInt(array, i);
                if (v == Integer.MIN_VALUE)
                    w.write("0N");
                else if (v == Integer.MAX_VALUE)
                    w.write("0W");
                else if (v == -Integer.MAX_VALUE)
                    w.write("-0W");
                else {
                    printD = false;
                    w.write(Config.getInstance().getDateTimeFormatter("yyyy.MM.dd").format(Instant.ofEpochMilli(MILLIS_IN_DAY * (v + DAYS_BETWEEN_1970_AND_2000))));
                }
            }
            if (printD)
                w.write("d");
        }
    }
}