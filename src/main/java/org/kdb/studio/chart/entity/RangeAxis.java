package org.kdb.studio.chart.entity;

public class RangeAxis extends DomainAxis<RangeAxis> {
    public Range range;
    public TickUnit tickUnit;
    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public TickUnit getTickUnit() {
        return tickUnit;
    }

    public void setTickUnit(TickUnit tickUnit) {
        this.tickUnit = tickUnit;
    }

    @Override
    public void override(RangeAxis obj) {
        Overridable.overrideObject(DomainAxis.class, this, obj);
        Overridable.overrideObject(this, obj);
    }
}
