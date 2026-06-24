package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import org.kdb.studio.ui.KDBToolbarUIManager;

public class ToolbarControlAction extends ToggleAction implements DumbAware {

    public ToolbarControlAction() {
        super("KDB+ Studio Toolbar");
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public boolean isSelected(AnActionEvent anActionEvent) {
        return ApplicationManager.getApplication().getService(KDBToolbarUIManager.class).isVisible();
    }

    @Override
    public void setSelected(AnActionEvent anActionEvent, boolean b) {
        if (b) {
            ApplicationManager.getApplication().getService(KDBToolbarUIManager.class).show();
        } else {
            ApplicationManager.getApplication().getService(KDBToolbarUIManager.class).hide();
        }
    }
}
