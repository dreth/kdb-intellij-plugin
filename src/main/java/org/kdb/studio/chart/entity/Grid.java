package org.kdb.studio.chart.entity;

public class Grid implements Overridable<Grid> {

    public boolean horizontal;

    public boolean vertical;

    public float horizontalLineWidth = 0.5f;

    public float verticalLineWidth = 0.5f;

    public String horizontalLineColor;

    public String verticalLineColor;

    public LineType horizontalLineType;

    public LineType verticalLineType;

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public float getHorizontalLineWidth() {
        return horizontalLineWidth;
    }

    public void setHorizontalLineWidth(float horizontalLineWidth) {
        this.horizontalLineWidth = horizontalLineWidth;
    }

    public float getVerticalLineWidth() {
        return verticalLineWidth;
    }

    public void setVerticalLineWidth(float verticalLineWidth) {
        this.verticalLineWidth = verticalLineWidth;
    }

    public String getHorizontalLineColor() {
        return horizontalLineColor;
    }

    public void setHorizontalLineColor(String horizontalLineColor) {
        this.horizontalLineColor = horizontalLineColor;
    }

    public String getVerticalLineColor() {
        return verticalLineColor;
    }

    public void setVerticalLineColor(String verticalLineColor) {
        this.verticalLineColor = verticalLineColor;
    }

    public LineType getHorizontalLineType() {
        return horizontalLineType;
    }

    public void setHorizontalLineType(LineType horizontalLineType) {
        this.horizontalLineType = horizontalLineType;
    }

    public LineType getVerticalLineType() {
        return verticalLineType;
    }

    public void setVerticalLineType(LineType verticalLineType) {
        this.verticalLineType = verticalLineType;
    }

    @Override
    public void override(Grid obj) {
        Overridable.overrideObject(this, obj);
    }
}
