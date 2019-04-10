package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.Instant;

public class KDatetimeVector extends KBaseVector {
    public String getDataType() {
        return "Datetime Vector";
    }

    public KDatetimeVector(int length) {
        super(double.class, length);
        type = 15;
    }

    public KBase at(int i) {
        return new KDatetime(Array.getDouble(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`datetime$()");
        else {
            boolean printZ = true;
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                double d = Array.getDouble(array, i);
                if (i > 0)
                    w.write(" ");
                if (Double.isNaN(d))
                    w.write("0N");
                else if (d == Double.POSITIVE_INFINITY)
                    w.write("0w");
                else if (d == Double.NEGATIVE_INFINITY)
                    w.write("-0w");
                else {
                    printZ = false;
                    w.write(Config.getInstance().getDateTimeFormatter("yyyy.MM.dd HH:mm:ss.SSS").format(Instant.ofEpochMilli(((long) (.5 + ((double) MILLIS_IN_DAY) * (d + DAYS_BETWEEN_1970_AND_2000))))));
                }
            }
            if (printZ)
                w.write("z");
        }
    }
}