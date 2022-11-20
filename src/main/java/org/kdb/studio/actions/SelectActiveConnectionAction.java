package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.db.Connection;
import org.kdb.studio.db.ConnectionManager;

public class SelectActiveConnectionAction extends AnAction {

    private Connection connection;

    private ConnectionManager connectionManager;

    public SelectActiveConnectionAction(Connection connection, ConnectionManager connectionManager) {
        super(connection.getView());
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/kx-kdb-logo.png", this.getClass().getClassLoader()));
        this.connection = connection;
        this.connectionManager = connectionManager;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        connectionManager.setActiveConnection(connection);
    }
}
