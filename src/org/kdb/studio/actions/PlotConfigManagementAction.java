package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.kdb.studio.ui.PlotConfigManagement;

public class PlotConfigManagementAction extends AnAction {

    public PlotConfigManagementAction() {
        super("Plot Config");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            new PlotConfigManagement(project).show();
        }
    }
}
