package org.kdb.studio.chart.entity;

public class TickUnit {
    public boolean autoSelection;
    public double tickUnitValue;

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
