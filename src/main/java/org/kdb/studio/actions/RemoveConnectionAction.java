package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.ConnectionsManagement;

public class RemoveConnectionAction extends AnAction {

    private ConnectionsManagement connectionsManagement;

    public RemoveConnectionAction(ConnectionsManagement connectionsManagement) {
        super("Remove connection");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/remove.png", this.getClass().getClassLoader()));
        this.connectionsManagement = connectionsManagement;
    }


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        connectionsManagement.removeCurrentValue();
    }
}
