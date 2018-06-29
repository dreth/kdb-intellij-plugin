package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;

public  class KShortVector extends KBaseVector {
        public String getDataType() {
            return "Short Vector";
        }

        public KShortVector(int length) {
            super(short.class,length);
            type = 5;
        }

        public KBase at(int i) {
            return new KShort(Array.getShort(array,i));
        }

        public void toString(LimitedWriter w, boolean showType) throws IOException {
            w.write(super.toString(showType));

            if (getLength() == 0)
                w.write("`short$()");
            else {
                if (getLength() == 1)
                    w.write(enlist);
                for (int i = 0;i < getLength();i++) {
                    if (i > 0)
                        w.write(" ");
                    short v = Array.getShort(array,i);
                    if (v == Short.MIN_VALUE)
                        w.write("0N");
                    else if (v == Short.MAX_VALUE)
                        w.write("0W");
                    else if (v == -Short.MAX_VALUE)
                        w.write("-0W");
                    else {
                        w.write("" + v);
                    }
                }
                if(showType)
                    w.write("h");
            }
        }
    }