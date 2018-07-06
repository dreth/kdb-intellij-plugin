package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

public class KDatetime extends KBase {
    public String getDataType() {
        return "Datetime";
    }

    double time;

    public KDatetime(double time) {
        type = -15;
        this.time = time;
    }

    public boolean isNull() {
        return Double.isNaN(time);
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0nz";
        else if (time == Double.POSITIVE_INFINITY)
            return "0wz";
        else if (time == Double.NEGATIVE_INFINITY)
            return "-0wz";
        else
            return Config.getInstance().getDateTimeFormatter("yyyy.MM.dd HH:mm:ss.SSS").format(toTimestamp());
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Instant toTimestamp() {
        return Instant.ofEpochMilli(((long) (.5 + ((double) MILLIS_IN_DAY) * (time + DAYS_BETWEEN_1970_AND_2000))));
    }
}