package org.kdb.studio;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.NonNls;

import java.awt.*;
import java.lang.ref.Reference;
import java.util.ResourceBundle;

public class KDBBundle {

    public static Color getKeyColumnBackgroundColor() {
        String colorTxt = CommonBundle.message(getBundle(),"key.column.background.color");
        try {
            return Color.decode(colorTxt);
        } catch (Exception e) {
            return new Color(220, 255, 220);
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
