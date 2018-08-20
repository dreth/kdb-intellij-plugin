package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.kdb.studio.ui.HTMLViewer;
import org.kdb.studio.ui.QGrid;

import javax.swing.*;

public class ShowAsHTMLAction extends AnAction {

    public ShowAsHTMLAction() {
        super("Show console output as HTML");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            QGrid qGrid = QGrid.getInstance(project, false);
            if (qGrid != null && !qGrid.isBlocked() && !qGrid.getTableGroup().isActive()) {
                JEditorPane consolePane = qGrid.getConsolePane();
                if ("text/plain".equals(consolePane.getContentType())) {
                    new HTMLViewer(project, consolePane.getText()).show();
                }
            }
        }
    }
}
