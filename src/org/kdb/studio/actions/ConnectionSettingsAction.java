package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.ConnectionsManagement;

public class ConnectionSettingsAction extends AnAction {

    private ConnectionManager connectionManager;

    public ConnectionSettingsAction(ConnectionManager connectionManager) {
        super("Edit connections...");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/editSource.png"));
        this.connectionManager = connectionManager;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            new ConnectionsManagement(project, connectionManager).show();
        }
    }
}
