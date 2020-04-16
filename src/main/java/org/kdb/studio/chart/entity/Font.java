package org.kdb.studio.chart.entity;

import java.util.ArrayList;
import java.util.List;

public class Font implements Overridable<Font>{

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

    public static Font fromAwtFont(java.awt.Font font) {
        Font dto = new Font();
        dto.setFont(font.getFamily());
        dto.setSize(font.getSize());
        dto.setAttributes(new ArrayList<>());
        dto.getAttributes().add(FontAttributes.PLAIN);
        if (font.isBold()) {
            dto.getAttributes().add(FontAttributes.BOLD);
        }
        if (font.isItalic()) {
            dto.getAttributes().add(FontAttributes.ITALIC);
        }
        return dto;
    }

    public static java.awt.Font toAwtFont(Font dto) {
        int style = java.awt.Font.PLAIN;
        if (dto.getAttributes() != null && dto.getAttributes().contains(FontAttributes.BOLD)) {
            style |= java.awt.Font.BOLD;
        }
        if (dto.getAttributes() != null && dto.getAttributes().contains(FontAttributes.ITALIC)) {
            style |= java.awt.Font.ITALIC;
        }
        return new java.awt.Font(dto.getFont(), style, dto.getSize());
    }

    @Override
    public void override(Font obj) {
        Overridable.overrideObject(this, obj);
        if (obj.attributes != null) {
            setAttributes(obj.attributes);
        }
    }
}
