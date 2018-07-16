package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.DumbAware;
import org.kdb.studio.ui.KDBToolbarUIManager;

public class ToolbarControlAction extends ToggleAction implements DumbAware {

    public ToolbarControlAction() {
        super("KDB+ Studio Toolbar");
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return KDBToolbarUIManager.getInstance().isVisible();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean b) {
        if (b) {
            KDBToolbarUIManager.getInstance().show();
        } else {
            KDBToolbarUIManager.getInstance().hide();
        }
    }
}
