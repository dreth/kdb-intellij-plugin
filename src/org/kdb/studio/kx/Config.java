package org.kdb.studio.kx;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class Config {
    private static final Config INSTANCE = new Config();

    public static Config getInstance() {
        return INSTANCE;
    }

    private String encoding;

    private NumberFormat numberFormat;

    private NumberFormat nanosFormat;

    public Config() {
        encoding = "UTF-8";
        numberFormat = new DecimalFormat("#.#######");
        nanosFormat = new DecimalFormat("000000000");
    }

    public String getEncoding() {
        return encoding;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public SimpleDateFormat getDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public NumberFormat getNanosFormat() {
        return nanosFormat;
    }
}
