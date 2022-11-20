package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.ConnectionsManagement;

public class CloneConnectionAction extends AnAction {

    private ConnectionsManagement connectionsManagement;

    public CloneConnectionAction(ConnectionsManagement connectionsManagement) {
        super("Clone connection");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/clone.png", this.getClass().getClassLoader()));
        this.connectionsManagement = connectionsManagement;
    }


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        connectionsManagement.cloneCurrentValue();
    }
}
