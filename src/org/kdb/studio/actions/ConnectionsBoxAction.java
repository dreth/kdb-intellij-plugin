package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.db.Connection;
import org.kdb.studio.db.ConnectionManager;

import javax.swing.*;
import java.awt.*;

public class ConnectionsBoxAction extends ComboBoxAction {

    private ConnectionManager connectionManager;

    private ComboBoxButton btn;

    private Color defaultBg;

    public ConnectionsBoxAction(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        Presentation presentation = e.getPresentation();
        Connection connection = connectionManager.getActiveConnection();
        if (connection == null) {
            presentation.setText("<Select Connection>");
        } else {
            presentation.setIcon(IconLoader.findIcon("/icons/kx-kdb-logo.png"));
            presentation.setText(connection.getView());
            if (btn != null) {
                if  (connection.getBgColor() != null) {
                    btn.setBackground(Color.decode(connection.getBgColor()));
                } else {
                    btn.setBackground(defaultBg);
                }
            }
        }
    }

    @Override
    protected ComboBoxButton createComboBoxButton(Presentation presentation) {
        this.btn = super.createComboBoxButton(presentation);
        this.defaultBg = this.btn.getBackground();
        return this.btn;
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent jComponent) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        for (Connection connection : connectionManager.getConnections(true)) {
            actionGroup.add(new SelectActiveConnectionAction(connection, connectionManager));
        }
        actionGroup.addSeparator();
        actionGroup.add(new ConnectionSettingsAction(connectionManager));
        return actionGroup;
    }
}