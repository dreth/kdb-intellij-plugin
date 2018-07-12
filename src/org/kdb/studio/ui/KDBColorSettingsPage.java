package org.kdb.studio.ui;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class KDBColorSettingsPage implements ColorSettingsPage {

    public static ColorKey KDB_CONSOLE_BACKGROUND = ColorKey.createColorKey("KDB_CONSOLE_BACKGROUND", ConsoleViewContentType.CONSOLE_BACKGROUND_KEY);

    public static ColorKey KDB_CONSOLE_FOREGROUND = ColorKey.createColorKey("KDB_CONSOLE_FOREGROUND", UIManager.getColor("EditorPane.foreground"));

    public static ColorKey KDB_KEY_COLUMN_BACKGROUND = ColorKey.createColorKey("KDB_KEY_COLUMN_BACKGROUND", new Color(220, 255, 220));

    public static ColorKey KDB_ODD_COLUMN_BACKGROUND = ColorKey.createColorKey("KDB_ODD_COLUMN_BACKGROUND",new Color(220,220,255));

    public static ColorKey KDB_NULL_COLUMN_FOREGROUND = ColorKey.createColorKey("KDB_NULL_COLUMN_FOREGROUND",new Color(255,150,150));

    public static ColorKey KDB_TABLE_SELECTION_BACKGROUND = ColorKey.createColorKey("KBD_SELECTION_BACKGROUND", EditorColors.SELECTION_BACKGROUND_COLOR);

    public static ColorKey KDB_TABLE_SELECTION_FOREGROUND = ColorKey.createColorKey("KBD_SELECTION_FOREGROUND", EditorColors.SELECTION_FOREGROUND_COLOR);

    public static ColorKey KDB_TABLE_BACKGROUND = ColorKey.createColorKey("KDB_TABLE_BACKGROUND", UIManager.getColor("Table.background"));

    public static ColorKey KDB_TABLE_FOREGROUND = ColorKey.createColorKey("KDB_TABLE_FOREGROUND", UIManager.getColor("Table.foreground"));


    private static final ColorDescriptor[] DESCRIPTORS = new ColorDescriptor[] {
            new ColorDescriptor("Console//Background color", KDB_CONSOLE_BACKGROUND, ColorDescriptor.Kind.BACKGROUND),
            new ColorDescriptor("Console//Foreground color", KDB_CONSOLE_FOREGROUND, ColorDescriptor.Kind.FOREGROUND),
            new ColorDescriptor("Table//Key Column background", KDB_KEY_COLUMN_BACKGROUND, ColorDescriptor.Kind.BACKGROUND),
            new ColorDescriptor("Table//Odd Column background", KDB_ODD_COLUMN_BACKGROUND,  ColorDescriptor.Kind.BACKGROUND),
            new ColorDescriptor("Table//Null Column foreground", KDB_NULL_COLUMN_FOREGROUND, ColorDescriptor.Kind.FOREGROUND),
            new ColorDescriptor("Table//Selection background", KDB_TABLE_SELECTION_BACKGROUND, ColorDescriptor.Kind.BACKGROUND),
            new ColorDescriptor("Table//Selection foreground", KDB_TABLE_SELECTION_FOREGROUND, ColorDescriptor.Kind.FOREGROUND),
            new ColorDescriptor("Table//Background", KDB_TABLE_BACKGROUND, ColorDescriptor.Kind.BACKGROUND),
            new ColorDescriptor("Table//Foreground", KDB_TABLE_FOREGROUND, ColorDescriptor.Kind.FOREGROUND)
    };

    private static final AttributesDescriptor[] ATTRS = new AttributesDescriptor[]{
    };


    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PlainSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "KDB+";
    }
}
