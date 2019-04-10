package org.kdb.studio.ui;

import com.intellij.openapi.editor.colors.impl.FontPreferencesImpl;

import java.awt.*;

public class MyFontPreferences extends FontPreferencesImpl {

    private int fontAttributes = Font.PLAIN;

    public boolean isBold() {
        return (fontAttributes & Font.BOLD) != 0;
    }

    public boolean isItalic() {
        return (fontAttributes & Font.ITALIC) != 0;
    }

    public void setBold(boolean isSet) {
        fontAttributes = isSet ? fontAttributes | Font.BOLD : (fontAttributes | Font.BOLD) ^ Font.BOLD;
    }

    public void setItalic(boolean isSet) {
        fontAttributes = isSet ? fontAttributes | Font.ITALIC : (fontAttributes | Font.ITALIC) ^ Font.ITALIC;
    }
}
