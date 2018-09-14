package org.kdb.studio.chart.entity;

public class DomainAxis {
    public Label label;
    public Tick ticks;
    public String axisLineColor;
    public String tickMarkColor;

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Tick getTicks() {
        return ticks;
    }

    public void setTicks(Tick ticks) {
        this.ticks = ticks;
    }

    public String getAxisLineColor() {
        return axisLineColor;
    }

    public void setAxisLineColor(String axisLineColor) {
        this.axisLineColor = axisLineColor;
    }

    public String getTickMarkColor() {
        return tickMarkColor;
    }

    public void setTickMarkColor(String tickMarkColor) {
        this.tickMarkColor = tickMarkColor;
    }
}
