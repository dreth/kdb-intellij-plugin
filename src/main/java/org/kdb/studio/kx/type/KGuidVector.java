package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.UUID;

public class KGuidVector extends KBaseVector {
    public String getDataType() {
        return "Guid Vector";
    }


    public KGuidVector(int length) {
        super(UUID.class, length);
        type = 2;
    }

    public KBase at(int i) {
        return new KGuid((UUID) Array.get(array, i));
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(super.toString(showType));

        if (getLength() == 0)
            w.write("`guid$()");
        else {
            if (getLength() == 1)
                w.write(enlist);
            for (int i = 0; i < getLength(); i++) {
                if (i > 0)
                    w.write(" ");
                w.write(((UUID) Array.get(array, i)).toString());
            }
        }
    }
}