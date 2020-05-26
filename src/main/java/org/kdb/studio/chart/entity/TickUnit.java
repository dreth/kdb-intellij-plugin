package org.kdb.studio.chart.entity;

public class TickUnit implements Overridable<TickUnit> {
    public boolean autoSelection;
    public double tickUnitValue;

    @Override
    public void override(TickUnit obj) {
        Overridable.overrideObject(this, obj);
    }

    public boolean isAutoSelection() {
        return autoSelection;
    }

    public void setAutoSelection(boolean autoSelection) {
        this.autoSelection = autoSelection;
    }

    public double getTickUnitValue() {
        return tickUnitValue;
    }

    public void setTickUnitValue(double tickUnitValue) {
        this.tickUnitValue = tickUnitValue;
    }
}
