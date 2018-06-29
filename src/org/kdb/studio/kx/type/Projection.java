package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class Projection extends KBase {
    public String getDataType() {
        return "Projection";
    }

    private KList objs;

    public Projection(KList objs) {
        type = 104;
        this.objs = objs;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        boolean listProjection = false;
        if ((objs.getLength() > 0) && (objs.at(0) instanceof UnaryPrimitive)) {
            UnaryPrimitive up = (UnaryPrimitive) objs.at(0);
            if (up.getPrimitiveAsInt() == 41) // plist
                listProjection = true;
        }

        if (listProjection) {
            w.write("(");
            for (int i = 1; i < objs.getLength(); i++) {
                if (i > 1)
                    w.write(";");

                objs.at(i).toString(w, showType);
            }
            w.write(")");
        } else {
            boolean isFunction = false;

            for (int i = 0; i < objs.getLength(); i++) {
                if (i == 0)
                    if ((objs.at(0) instanceof Function) || (objs.at(0) instanceof UnaryPrimitive) || (objs.at(0) instanceof BinaryPrimitive))
                        isFunction = true;
                    else
                        w.write("(");

                if (i > 0)
                    if (i == 1)
                        if (isFunction)
                            w.write("[");
                        else
                            w.write(";");
                    else
                        w.write(";");

                objs.at(i).toString(w, showType);
            }

            if (isFunction)
                w.write("]");
            else
                w.write(")");
        }
    }
}