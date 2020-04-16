package org.kdb.studio.chart.entity;

public class Label implements Overridable<Label>{
    public Boolean show = false;
    public String text;
    public Font font;
    public String color;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public void override(Label obj) {
        Overridable.overrideObject(this, obj);
    }
}
