package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.ConnectionsManagement;

public class AddConnectionAction extends AnAction {

    private ConnectionsManagement connectionsManagement;

    public AddConnectionAction(ConnectionsManagement connectionsManagement) {
        super("Add connection");
        this.connectionsManagement = connectionsManagement;
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/add.png", this.getClass().getClassLoader()));
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        this.connectionsManagement.setEmptyValue();
    }
}
