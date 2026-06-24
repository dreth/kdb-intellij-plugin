package org.kdb.studio.ui;

import com.intellij.openapi.editor.colors.FontPreferences;
import com.intellij.openapi.editor.colors.ModifiableFontPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyFontPreferences extends ModifiableFontPreferences {

    private final List<String> effectiveFontFamilies = new ArrayList<>();
    private final List<String> realFontFamilies = new ArrayList<>();
    private final Map<String, Integer> fontSizes = new LinkedHashMap<>();
    private int fontAttributes = Font.PLAIN;
    private boolean useLigatures;
    private float lineSpacing = DEFAULT_LINE_SPACING;
    private String regularSubFamily;
    private String boldSubFamily;
    private Set<String> characterVariants = new LinkedHashSet<>();

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

    @Override
    public void clear() {
        clearFonts();
        fontAttributes = Font.PLAIN;
        useLigatures = false;
        lineSpacing = DEFAULT_LINE_SPACING;
        regularSubFamily = null;
        boldSubFamily = null;
        characterVariants = new LinkedHashSet<>();
    }

    @Override
    public void clearFonts() {
        effectiveFontFamilies.clear();
        realFontFamilies.clear();
        fontSizes.clear();
    }

    @Override
    public @NotNull List<String> getEffectiveFontFamilies() {
        return effectiveFontFamilies;
    }

    @Override
    public @NotNull List<String> getRealFontFamilies() {
        return realFontFamilies;
    }

    @Override
    public @NotNull String getFontFamily() {
        if (!effectiveFontFamilies.isEmpty()) {
            return effectiveFontFamilies.get(0);
        }
        if (!realFontFamilies.isEmpty()) {
            return realFontFamilies.get(0);
        }
        return DEFAULT_FONT_NAME;
    }

    @Override
    public @Nullable String getRegularSubFamily() {
        return regularSubFamily;
    }

    @Override
    public @Nullable String getBoldSubFamily() {
        return boldSubFamily;
    }

    @Override
    public int getSize(@NotNull String fontFamily) {
        Integer size = fontSizes.get(fontFamily);
        if (size != null) {
            return size;
        }
        return fontSizes.values().stream().findFirst().orElse(DEFAULT_FONT_SIZE);
    }

    @Override
    public boolean hasSize(@NotNull String fontName) {
        return fontSizes.containsKey(fontName);
    }

    @Override
    public float getLineSpacing() {
        return lineSpacing;
    }

    @Override
    public boolean useLigatures() {
        return useLigatures;
    }

    @Override
    public void copyTo(@NotNull FontPreferences preferences) {
        if (preferences instanceof MyFontPreferences myFontPreferences) {
            myFontPreferences.effectiveFontFamilies.clear();
            myFontPreferences.effectiveFontFamilies.addAll(effectiveFontFamilies);
            myFontPreferences.realFontFamilies.clear();
            myFontPreferences.realFontFamilies.addAll(realFontFamilies);
            myFontPreferences.fontSizes.clear();
            myFontPreferences.fontSizes.putAll(fontSizes);
            myFontPreferences.fontAttributes = fontAttributes;
            myFontPreferences.useLigatures = useLigatures;
            myFontPreferences.lineSpacing = lineSpacing;
            myFontPreferences.regularSubFamily = regularSubFamily;
            myFontPreferences.boldSubFamily = boldSubFamily;
            myFontPreferences.characterVariants = new LinkedHashSet<>(characterVariants);
        }
        else if (preferences instanceof ModifiableFontPreferences modifiablePreferences) {
            modifiablePreferences.clear();
            effectiveFontFamilies.forEach(modifiablePreferences::addFontFamily);
            fontSizes.forEach(modifiablePreferences::setFontSize);
            modifiablePreferences.setUseLigatures(useLigatures);
            modifiablePreferences.setLineSpacing(lineSpacing);
            modifiablePreferences.setRegularSubFamily(regularSubFamily);
            modifiablePreferences.setBoldSubFamily(boldSubFamily);
            modifiablePreferences.setCharacterVariants(characterVariants);
        }
    }

    @Override
    public void setUseLigatures(boolean useLigatures) {
        this.useLigatures = useLigatures;
    }

    @Override
    public void addFontFamily(String family) {
        if (!effectiveFontFamilies.contains(family)) {
            effectiveFontFamilies.add(family);
        }
        if (!realFontFamilies.contains(family)) {
            realFontFamilies.add(family);
        }
    }

    @Override
    public void register(String family, int size) {
        addFontFamily(family);
        setFontSize(family, size);
    }

    @Override
    public void setEffectiveFontFamilies(List fontFamilies) {
        effectiveFontFamilies.clear();
        fontFamilies.forEach(fontFamily -> effectiveFontFamilies.add(String.valueOf(fontFamily)));
    }

    @Override
    public void setRealFontFamilies(List fontFamilies) {
        realFontFamilies.clear();
        fontFamilies.forEach(fontFamily -> realFontFamilies.add(String.valueOf(fontFamily)));
    }

    @Override
    public void setTemplateFontSize(int size) {
        for (String fontFamily : effectiveFontFamilies) {
            fontSizes.put(fontFamily, size);
        }
    }

    @Override
    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    @Override
    public void resetFontSizes() {
        fontSizes.clear();
    }

    @Override
    public void setFontSize(@NotNull String fontFamily, int size) {
        fontSizes.put(fontFamily, size);
    }

    public void setSize(@NotNull String fontFamily, int size) {
        setFontSize(fontFamily, size);
    }

    @Override
    public void setRegularSubFamily(String subFamily) {
        regularSubFamily = subFamily;
    }

    @Override
    public void setBoldSubFamily(String subFamily) {
        boldSubFamily = subFamily;
    }

    @Override
    public @Unmodifiable @NotNull Set<@NotNull String> getCharacterVariants() {
        return Set.copyOf(characterVariants);
    }

    @Override
    public void setCharacterVariants(@Unmodifiable @NotNull Set<@NotNull String> variants) {
        characterVariants = new LinkedHashSet<>(variants);
    }

    @Override
    public void setCharacterVariant(@NotNull String variant, boolean enabled) {
        if (enabled) {
            characterVariants.add(variant);
        }
        else {
            characterVariants.remove(variant);
        }
    }
}
