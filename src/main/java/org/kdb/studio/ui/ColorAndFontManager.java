package org.kdb.studio.ui;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ColorAndFontManager {

    public static ColorKey KDB_CONSOLE_BACKGROUND = ColorKey.createColorKey("KDB_CONSOLE_BACKGROUND", UIManager.getColor("EditorPane.background"));

    public static ColorKey KDB_CONSOLE_FOREGROUND = ColorKey.createColorKey("KDB_CONSOLE_FOREGROUND", UIManager.getColor("EditorPane.foreground"));

    public static ColorKey KDB_KEY_COLUMN_BACKGROUND = ColorKey.createColorKey("KDB_KEY_COLUMN_BACKGROUND", new Color(220, 255, 220));

    public static ColorKey KDB_ODD_COLUMN_BACKGROUND = ColorKey.createColorKey("KDB_ODD_COLUMN_BACKGROUND",new Color(220,220,255));

    public static ColorKey KDB_NULL_COLUMN_FOREGROUND = ColorKey.createColorKey("KDB_NULL_COLUMN_FOREGROUND",new Color(255,150,150));

    public static ColorKey KDB_TABLE_SELECTION_BACKGROUND = ColorKey.createColorKey("KBD_SELECTION_BACKGROUND", UIManager.getColor("Table.selectionBackground"));

    public static ColorKey KDB_TABLE_SELECTION_FOREGROUND = ColorKey.createColorKey("KBD_SELECTION_FOREGROUND", UIManager.getColor("Table.selectionForeground"));

    public static ColorKey KDB_TABLE_BACKGROUND = ColorKey.createColorKey("KDB_TABLE_BACKGROUND", UIManager.getColor("Table.background"));

    public static ColorKey KDB_TABLE_FOREGROUND = ColorKey.createColorKey("KDB_TABLE_FOREGROUND", UIManager.getColor("Table.foreground"));

    public static String CONSOLE_FONT = "Console window";

    public static String TABLE_CONTENT_FONT = "Table contents";

    public static String TABLE_HEADER_FONT = "Table headers";

    public static String TABLE_ROW_NUM_FONT = "Row numbers";

    private static ColorAndFontManager INSTANCE = new ColorAndFontManager();

    private EditorColorsScheme scheme;

    private Map<String, Font> fontMap;

    private boolean formattingEnabled = true;

    private ColorAndFontManager() {
        scheme = EditorColorsManager.getInstance().getGlobalScheme();
        fontMap = new LinkedHashMap<>();
    }

    public static ColorAndFontManager getInstance() {
        return INSTANCE;
    }

    public Color getColor(ColorKey key) {
        return scheme.getColor(key);
    }

    public Font getFont(String key) {
        return fontMap.getOrDefault(key, Font.decode("Monospaced-12"));
    }

    public Map<String, MyFontPreferences> getFontPreferences() {
        Map<String, MyFontPreferences> allPreferences = new LinkedHashMap<>();
        allPreferences.put(CONSOLE_FONT, toFontPreferences(getFont(CONSOLE_FONT)));
        allPreferences.put(TABLE_CONTENT_FONT, toFontPreferences(getFont(TABLE_CONTENT_FONT)));
        allPreferences.put(TABLE_HEADER_FONT, toFontPreferences(getFont(TABLE_HEADER_FONT)));
        allPreferences.put(TABLE_ROW_NUM_FONT, toFontPreferences(getFont(TABLE_ROW_NUM_FONT)));
        return allPreferences;
    }

    public void updateFontPreferences(Map<String, MyFontPreferences> allPreferences, Project project) {
        allPreferences.entrySet().forEach(entry -> fontMap.put(entry.getKey(), fromFontPreferences(entry.getValue())));
        if (project != null) {
            QGrid.getInstance(project, false).updateStyles();
        }
    }

    protected MyFontPreferences toFontPreferences(Font font) {
        MyFontPreferences myFontPreferences = new MyFontPreferences();
        myFontPreferences.clearFonts();
        myFontPreferences.register(font.getFamily(), font.getSize());
        myFontPreferences.setItalic(font.isItalic());
        myFontPreferences.setBold(font.isBold());
        return myFontPreferences;
    }

    protected Font fromFontPreferences(MyFontPreferences fontPreferences) {
        int style = Font.PLAIN;
        if (fontPreferences.isBold()) {
            style |= Font.BOLD;
        }
        if (fontPreferences.isItalic()) {
            style |= Font.ITALIC;
        }
        return new Font(fontPreferences.getFontFamily(), style, fontPreferences.getSize(fontPreferences.getFontFamily()));
    }

    public Map<String, Font> getFontMap() {
        return fontMap;
    }

    public boolean getFormattingEnabled() {
        return formattingEnabled;
    }

    public void setFormattingEnabled(boolean formattingEnabled) {
        this.formattingEnabled = formattingEnabled;
    }
}
