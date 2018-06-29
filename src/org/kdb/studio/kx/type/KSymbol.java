package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Config;
import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.io.OutputStream;

public  class KSymbol extends KBase {
        public String getDataType() {
            return "Symbol";
        }
        public String s;

        public KSymbol(String s) {
            this.s = s;
            type = -11;
        }

        public String toString(boolean showType) {
            return s;
        }

        public boolean isNull() {
            return s.length() == 0;
        }

        public void toString(LimitedWriter w, boolean showType) throws IOException {
            if (showType)
                w.write("`");
            w.write(s);
        }

        public void serialise(OutputStream o) throws IOException {
            o.write(s.getBytes(Config.getInstance().getEncoding()));
        }
    }