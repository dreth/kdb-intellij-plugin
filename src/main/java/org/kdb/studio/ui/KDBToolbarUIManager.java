package org.kdb.studio.ui;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;

public class KDBToolbarUIManager {

    public static final String VIEW_TOOLBAR = "ViewToolBar";

    static KDBToolbarUIManager INSTANCE;

    private boolean visible;

    public static void initInstance(boolean visible) {
        INSTANCE = new KDBToolbarUIManager(visible);
    }

    public static KDBToolbarUIManager getInstance() {
        return INSTANCE;
    }

    private KDBToolbarUIManager(boolean visible) {
        setState(visible);
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
    }

    public void show() {
        try {
            final AnActionEvent event = new AnActionEvent(null, DataManager.getInstance().getDataContext(), "KDB_TOOLBAR", new Presentation(),
                    ActionManager.getInstance(), 0);
            ActionManager actionManager = ActionManager.getInstance();
            ToggleAction viewToolbar = (ToggleAction) actionManager.getAction(VIEW_TOOLBAR);
            if (!viewToolbar.isSelected(event)) {
                viewToolbar.setSelected(event, true);
            }
        } catch (Exception ignore) {

        }
        visible = true;
    }
}
