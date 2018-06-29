package org.kdb.studio.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.db.Connection;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.kx.Connector;
import org.kdb.studio.kx.ConnectorFactory;
import org.kdb.studio.kx.type.KCharacterVector;
import org.kdb.studio.kx.type.KSymbolVector;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ConnectionsManagement extends DialogWrapper {

    private JPanel centralPanel;
    private JList connectionsList;
    private JButton btnNewConnection;
    private JButton btnRemoveConnection;
    private JTextField textName;
    private JTextField textHost;
    private JTextField textPort;
    private JTextField textUsername;
    private JPasswordField passwordField1;
    private JLabel validationMessage;
    private JLabel succedValidationMessage;
    private JPanel leftPanel;
    private SettingPanel settingsPanel;

    private ConnectionManager connectionManager;
    private Project project;

    public ConnectionsManagement(@Nullable Project project, ConnectionManager connectionManager) {
        super(project);
        this.project = project;
        this.connectionManager = connectionManager;
        setTitle("Available connections");

        connectionsList.setModel(new AbstractListModel() {
            @Override
            public int getSize() {
                return connectionManager.getConnections().length;
            }

            @Override
            public Object getElementAt(int index) {
                return connectionManager.getConnections()[index].getView();
            }
        });
        connectionsList.addListSelectionListener(e -> setAsCurrentValue(connectionManager.getConnectionByName((String) connectionsList.getSelectedValue())));
        setEmptyValue();
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return centralPanel;
    }

    @Override
    protected void doOKAction() {
        if (!hasValidationErrors()) {
            saveCurrentValues();

            Connection[] connections = connectionManager.getConnections();
            if (connectionManager.getActiveConnection() == null && connections.length > 0) {
                connectionManager.setActiveConnection(connections[connections.length - 1]);
            }

            super.doOKAction();
        }
    }

    protected boolean hasValidationErrors() {
        String message = "";

        try {
            Integer.parseInt(textPort.getText());
        } catch (Exception e) {
            message = "Port is invalid. Expected integer value.";
        }
        if (StringUtil.isEmptyOrSpaces(textHost.getText())) {
            message = "Host must not be empty";
        }
        if (StringUtil.isEmptyOrSpaces(textName.getText())) {
            message = "Name must not be empty";
        }

        validationMessage.setText(message);
        succedValidationMessage.setText("");
        return !StringUtil.isEmpty(message);
    }

    protected void saveCurrentValues() {
        Connection connection = new Connection(textName.getText(), textHost.getText(), Integer.parseInt(textPort.getText()), textUsername.getText(), passwordField1.getPassword());
        connectionManager.addOrUpdate(connection);
        connectionsList.updateUI();
        connectionsList.setSelectedValue(connection.getView(), true);
    }

    public void removeCurrentValue() {
        Connection connection = connectionManager.getConnectionByName((String) connectionsList.getSelectedValue());
        if (connection != null) {
            setEmptyValue();
            connectionManager.remove(connection);
            connectionsList.updateUI();
        }
    }

    protected void testCurrentValues() {
        Connection connection = new Connection(textName.getText(), textHost.getText(), Integer.parseInt(textPort.getText()), textUsername.getText(), passwordField1.getPassword());
        try (Connector connector = new ConnectorFactory(connection).create()) {
            connector.query(new KCharacterVector("key`.q"), KSymbolVector.class, project);
            validationMessage.setText("");
            succedValidationMessage.setText("Valid.");
        } catch (Throwable e) {
            succedValidationMessage.setText("");
            validationMessage.setText(e.getMessage());
        }

    }

    protected void setAsCurrentValue(Connection connection) {
        if (connection == null) {
            setEmptyValue();
        } else {
            textName.setText(connection.getName());
            textHost.setText(connection.getHost());
            textPort.setText(String.valueOf(connection.getPort()));
            textUsername.setText(connection.getUsername());
            passwordField1.setText(new String(connection.getPassword()));
            validationMessage.setText("");
            succedValidationMessage.setText("");
        }
    }

    public void setEmptyValue() {
        textName.setText("");
        textHost.setText("");
        textPort.setText(String.valueOf(0));
        textUsername.setText("");
        passwordField1.setText("");
        connectionsList.setSelectedIndices(new int[]{});
        validationMessage.setText("");
        succedValidationMessage.setText("");
    }

    @NotNull
    @Override
    protected Action[] createLeftSideActions() {
        return new Action[]{new ApplyAction(), new TestAction()};
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        settingsPanel = new SettingPanel(this);
    }

    protected class ApplyAction extends DialogWrapper.DialogWrapperAction {
        private ApplyAction() {
            super("Apply");
        }

        protected void doAction(ActionEvent e) {
            if (!ConnectionsManagement.this.hasValidationErrors()) {
                ConnectionsManagement.this.saveCurrentValues();
            }
        }
    }

    protected class TestAction extends DialogWrapper.DialogWrapperAction {
        private TestAction() {
            super("Test");
        }

        protected void doAction(ActionEvent e) {
            if (!ConnectionsManagement.this.hasValidationErrors()) {
                ConnectionsManagement.this.testCurrentValues();
            }

        }
    }
}
