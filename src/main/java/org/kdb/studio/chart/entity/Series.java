package org.kdb.studio.chart.entity;

public class Series {
    public String color;
    public String fillColor;
    public String outlineColor;
    public LineType lineType;
    public Boolean visibleInLegend;
    public Boolean show;
    public Marker marker;
    public ChartType type;
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

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(String outlineColor) {
        this.outlineColor = outlineColor;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }
}
