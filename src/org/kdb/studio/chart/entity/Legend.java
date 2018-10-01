package org.kdb.studio.chart.entity;

public class Legend {
    public boolean show;
    public String backgroundColor;
    public RectangleInsets margin;
    public Position position;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public RectangleInsets getMargin() {
        return margin;
    }

    public void setMargin(RectangleInsets margin) {
        this.margin = margin;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
