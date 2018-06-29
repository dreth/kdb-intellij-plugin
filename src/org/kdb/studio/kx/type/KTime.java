package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.sql.Time;

public class KTime extends KBase {
    public String getDataType() {
        return "Time";
    }

    int time;

    public KTime(int time) {
        type = -19;
        this.time = time;
    }

    public boolean isNull() {
        return time == Integer.MIN_VALUE;
    }

    public String toString(boolean showType) {
        if (isNull())
            return "0Nt";
        else if (time == Integer.MAX_VALUE)
            return "0Wt";
        else if (time == -Integer.MAX_VALUE)
            return "-0Wt";
        else
            return Config.getInstance().getDateFormat("HH:mm:ss.SSS").format(new Time(time));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }

    public Time toTime() {
        return new Time(time);
    }
}