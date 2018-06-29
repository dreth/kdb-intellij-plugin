package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;

public class Function extends KBase {
    public String getDataType() {
        return "Function";
    }

    ;
    private String body;

    public Function(KCharacterVector body) {
        type = 100;
        this.body = new String((char[]) body.getArray(), 0, body.getLength());
    }

    public String getBody() {
        return body;
    }

    public String toString(boolean showType) {
        return body;
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(body);
    }
}