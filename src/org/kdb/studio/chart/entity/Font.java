package org.kdb.studio.chart.entity;

import java.util.List;

public class Font {

    public int size;

    public List<FontAttributes> attributes;

    public String font;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<FontAttributes> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<FontAttributes> attributes) {
        this.attributes = attributes;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
