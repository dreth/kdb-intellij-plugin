package org.kdb.studio.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.kdb.studio.actions.AddConnectionAction;
import org.kdb.studio.actions.CloneConnectionAction;
import org.kdb.studio.actions.RemoveConnectionAction;

import javax.swing.*;
import java.awt.*;

public class SettingPanel extends JPanel {
    private ConnectionsManagement connectionsManagement;

    public SettingPanel(ConnectionsManagement connectionsManagement) {
        this.connectionsManagement = connectionsManagement;
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
    }

    private JComponent createToolBar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AddConnectionAction(connectionsManagement));
        actionGroup.add(new RemoveConnectionAction(connectionsManagement));
        actionGroup.add(new CloneConnectionAction(connectionsManagement));
        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar toolbar = actionManager.createActionToolbar("KDBStudio.SettingsPanel", actionGroup, true);
        JComponent component = toolbar.getComponent();
        component.getInsets().set(0, 0, 0, 0);
        return component;
    }
}
