package org.kdb.studio.kx.type;

import org.kdb.studio.kx.LimitedWriter;

import java.io.IOException;
import java.util.UUID;

public class KGuid extends KBase {
    static UUID nuuid = new UUID(0, 0);

    public String getDataType() {
        return "Guid";
    }

    UUID uuid;

    public KGuid(UUID uuid) {
        type = -2;
        this.uuid = uuid;
    }

    public boolean isNull() {
        return uuid == nuuid;
    }

    public String toString(boolean showType) {
        return uuid.toString();
    }

    public void toString(LimitedWriter w, boolean showType) throws IOException {
        w.write(toString(showType));
    }
}