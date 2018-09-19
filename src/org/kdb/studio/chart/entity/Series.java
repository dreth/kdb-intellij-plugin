package org.kdb.studio.chart.entity;

public class Series {
    public String color;
    public LineType lineType;
    public Boolean visibleInLegend;
    public Boolean show;
    public float lineWidth = 1f;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    public Boolean getVisibleInLegend() {
        return visibleInLegend;
    }

    public void setVisibleInLegend(Boolean visibleInLegend) {
        this.visibleInLegend = visibleInLegend;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
}
