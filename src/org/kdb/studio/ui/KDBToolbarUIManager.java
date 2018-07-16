package org.kdb.studio.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;

public class KDBToolbarUIManager {

    public static final String MAIN_TOOL_BAR = "MainToolBar";

    public static final String VIEW_TOOLBAR = "ViewToolBar";

    static KDBToolbarUIManager INSTANCE;

    private DefaultActionGroup actionGroup;

    private boolean visible;

    public static void initInstance(DefaultActionGroup actionGroup, boolean visible) {
        INSTANCE = new KDBToolbarUIManager(actionGroup, visible);
    }

    public static KDBToolbarUIManager getInstance() {
        return INSTANCE;
    }

    private KDBToolbarUIManager(DefaultActionGroup actionGroup, boolean visible) {
        this.actionGroup = actionGroup;
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
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup windowM = (DefaultActionGroup) actionManager.getAction(MAIN_TOOL_BAR);
        windowM.remove(actionGroup);
        visible = false;
    }

    public void show() {
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup windowM = (DefaultActionGroup) actionManager.getAction(MAIN_TOOL_BAR);
        windowM.add(actionGroup);
        ToggleAction viewToolbar = (ToggleAction)actionManager.getAction(VIEW_TOOLBAR);
        if (!viewToolbar.isSelected(null)) {
            viewToolbar.setSelected(null, true);
        }
        visible = true;
    }
}
