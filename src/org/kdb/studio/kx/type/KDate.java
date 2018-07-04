package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class KDate extends KBase {
    public String getDataType() {
        return "Date";
    }

    int date;

    public KDate(int date) {
        type = -14;
        this.date = date;
    }

    public boolean isNull() {
        return date == Integer.MIN_VALUE;
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0Nd";
        else if (date == Integer.MAX_VALUE)
            return "0Wd";
        else if (date == -Integer.MAX_VALUE)
            return "-0Wd";
        else
            return Config.getInstance().getDateTimeFormatter(("yyyy.MM.dd")).format(toDate());
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Instant toDate() {
        return Instant.ofEpochMilli(86400000L * (date + 10957));
    }
}