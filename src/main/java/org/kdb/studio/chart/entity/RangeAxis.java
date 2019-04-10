package org.kdb.studio.chart.entity;

public class RangeAxis extends DomainAxis {
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

}
