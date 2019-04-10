package org.kdb.studio.chart.entity;

public class Range {
    public boolean autoAdjust;
    public double minimumRangeValue;
    public double maximumRangeValue;

    public boolean isAutoAdjust() {
        return autoAdjust;
    }

    public void setAutoAdjust(boolean autoAdjust) {
        this.autoAdjust = autoAdjust;
    }

    public double getMinimumRangeValue() {
        return minimumRangeValue;
    }

    public void setMinimumRangeValue(double minimumRangeValue) {
        this.minimumRangeValue = minimumRangeValue;
    }

    public double getMaximumRangeValue() {
        return maximumRangeValue;
    }

    public void setMaximumRangeValue(double maximumRangeValue) {
        this.maximumRangeValue = maximumRangeValue;
    }
}
