package org.kdb.studio.ui;

import com.intellij.ide.DataManager;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionUtil;

public class KDBToolbarUIManager {

    public static final String VIEW_TOOLBAR = "ViewToolBar";

    static KDBToolbarUIManager INSTANCE;

    private boolean visible;

    public KDBToolbarUIManager() {
        setState(true);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setState(boolean state) {
        if (state == visible) {
            return;
        }
        if (state) {
            show();
        } else {
            hide();
        }
    }



    public void hide() {
        visible = false;
        UISettings uiSettings = UISettings.getInstance();
        uiSettings.fireUISettingsChanged();
    }

    public void show() {
        visible = true;
        UISettings uiSettings = UISettings.getInstance();
        if (!uiSettings.getShowMainToolbar()) {
            uiSettings.setShowMainToolbar(true);
        }
        uiSettings.fireUISettingsChanged();
    }
}
