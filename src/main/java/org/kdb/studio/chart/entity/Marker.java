package org.kdb.studio.chart.entity;

public class Marker implements Overridable<Marker>{
    public MarkerType type;
    public int size;

    public MarkerType getType() {
        return type;
    }

    public void setType(MarkerType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void override(Marker obj) {
        Overridable.overrideObject(this, obj);
    }
}
