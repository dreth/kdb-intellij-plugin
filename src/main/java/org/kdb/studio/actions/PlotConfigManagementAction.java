package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.ui.PlotConfigManagement;

import javax.swing.*;

public class PlotConfigManagementAction extends AnAction {

    public PlotConfigManagementAction(@Nullable String text, @Nullable Icon icon) {
        super(text);
        getTemplatePresentation().setIcon(icon);
    }

    public PlotConfigManagementAction() {
        super("Plot Config");
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            PlotConfigManagement management = new PlotConfigManagement(project);
            management.show();
        }
    }
}
