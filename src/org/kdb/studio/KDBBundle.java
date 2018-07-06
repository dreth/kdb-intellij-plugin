package org.kdb.studio;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.Reference;
import java.util.ResourceBundle;

public class KDBBundle {

    public static Color getKeyColumnBackgroundColor() {
        try {
            String colorTxt = CommonBundle.message(getBundle(),"key.column.background.color");
            return Color.decode(colorTxt);
        } catch (Exception e) {
            return new Color(220, 255, 220);
        }
    }

    public static Color getOddColumnBackgroundColor() {
        try {
            String colorTxt = CommonBundle.message(getBundle(), "odd.column.background.color");
            return Color.decode(colorTxt);
        } catch (Exception e) {
            return new Color(220,220,255);
        }
    }

    public static Color getNullColumnForegroundColor() {
        try {
            String colorTxt = CommonBundle.message(getBundle(), "null.column.foreground.color");
            return Color.decode(colorTxt);
        } catch (Exception e) {
            return new Color(255,150,150);
        }
    }

    public static Font getTableColumnFont() {
        try {
            String fontText = CommonBundle.message(getBundle(), "column.font");
            return Font.decode(fontText);
        } catch (Exception e) {
            return UIManager.getFont("Table.font");
        }
    }

    private static Reference<ResourceBundle> ourBundle;

    @NonNls
    protected static final String PATH_TO_BUNDLE = "messages.KDBBundle";

    private KDBBundle() {
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(PATH_TO_BUNDLE);
            ourBundle = new SoftReference<>(bundle);
        }
        return bundle;
    }


}
