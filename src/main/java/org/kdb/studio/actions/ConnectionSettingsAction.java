package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.db.AuthenticationDriverManager;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.ConnectionsManagement;

public class ConnectionSettingsAction extends AnAction {

    private ConnectionManager connectionManager;

    private AuthenticationDriverManager authenticationDriverManager;

    public ConnectionSettingsAction(ConnectionManager connectionManager, AuthenticationDriverManager authenticationDriverManager) {
        super("Edit connections...");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/editSource.png", this.getClass().getClassLoader()));
        this.connectionManager = connectionManager;
        this.authenticationDriverManager = authenticationDriverManager;
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            new ConnectionsManagement(project, connectionManager, authenticationDriverManager).show();
        }
    }
}
