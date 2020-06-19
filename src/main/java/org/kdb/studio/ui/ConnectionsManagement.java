package org.kdb.studio.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.db.AuthenticationDriver;
import org.kdb.studio.db.AuthenticationDriverManager;
import org.kdb.studio.db.Connection;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.kx.Connector;
import org.kdb.studio.kx.ConnectorFactory;
import org.kdb.studio.kx.K4Exception;
import org.kdb.studio.kx.type.KBase;
import org.kdb.studio.kx.type.KCharacterVector;
import org.kdb.studio.kx.type.KSymbolVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.SocketException;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ConnectionsManagement extends DialogWrapper {

    public static final String EDIT = "<Edit...>";
    public static final String BASIC = "<Basic>";

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
    private JCheckBox useEnvCheckbox;
    private JTextField textVariable;
    private JLabel labelVariable;
    private JLabel labelPassword;
    private JCheckBox multilineCommentSupport;
    private JBCheckBox bgColorEnabled;
    private ColorPanel bgColor;
    private ComboBox authType;
    private MutableCollectionComboBoxModel<String> authTypeModel;

    private ConnectionManager connectionManager;
    private AuthenticationDriverManager authenticationDriverManager;
    private Project project;
    protected boolean testBlocked = false;

    public ConnectionsManagement(@Nullable Project project, ConnectionManager connectionManager, AuthenticationDriverManager authenticationDriverManager) {
        super(project);
        this.project = project;
        this.connectionManager = connectionManager;
        this.authenticationDriverManager = authenticationDriverManager;
        setTitle("Available connections");

        connectionsList.setModel(new AbstractListModel() {
            @Override
            public int getSize() {
                return connectionManager.getConnections().length;
            }

            @Override
            public Object getElementAt(int index) {
                return connectionManager.getConnections(true)[index].getView();
            }
        });
        connectionsList.addListSelectionListener(e -> setAsCurrentValue(connectionManager.getConnectionByName((String) connectionsList.getSelectedValue())));
        useEnvCheckbox.addActionListener(e -> passwordVisible(!useEnvCheckbox.isSelected()));
        bgColorEnabled.addActionListener(e -> bgColor.setEnabled(bgColorEnabled.isSelected()));
        setEmptyValue();
        authTypeModel = new MutableCollectionComboBoxModel<>();
        authType.setModel(authTypeModel);
        updateUI();
        authType.addActionListener(e -> {
            if (authType.getSelectedItem() == EDIT) {
                new AuthenticationDriverDialog(this.project, authenticationDriverManager).show();
                updateUI();
            }
        });
        init();
    }

    protected void updateUI() {
        List<String> drivers = Arrays.stream(authenticationDriverManager.getAuthenticationDrivers()).map(AuthenticationDriver::getName).collect(Collectors.toList());
        drivers.add(0, BASIC);
        drivers.add(EDIT);
        authTypeModel.update(drivers);
        authType.setSelectedItem(BASIC);
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
        Connection connection = new Connection(textName.getText(), textHost.getText(), Integer.parseInt(textPort.getText()), textUsername.getText(), passwordField1.getPassword(), useEnvCheckbox.isSelected(), textVariable.getText(), multilineCommentSupport.isSelected(), getCurrentBgColor(), getAuthType());
        connectionManager.addOrUpdate(connection);
        connectionsList.updateUI();
        connectionsList.setSelectedValue(connection.getView(), true);
    }

    public void cloneCurrentValue() {
        String view = (String) connectionsList.getSelectedValue();
        if (view != null) {
            Optional.ofNullable(connectionManager.getConnectionByName(view)).ifPresent(connection -> {
                Connection clone = connection.clone();
                clone.setName(prepareCloneName(clone, 1));
                connectionManager.addOrUpdate(clone);
                connectionsList.updateUI();
                connectionsList.setSelectedValue(clone.getView(), true);
            });
        }
    }

    private String prepareCloneName(Connection connection, int index) {
        String name = "Clone ";
        if (index > 1) {
            name += index + " ";
        }
        name += "of " + connection.getName();
        if (connectionManager.getConnectionByName(name) != null) {
            return prepareCloneName(connection, ++index);
        }
        return name;

    }

    public void removeCurrentValue() {
        Connection connection = connectionManager.getConnectionByName((String) connectionsList.getSelectedValue());
        if (connection != null) {
            setEmptyValue();
            connectionManager.remove(connection);
            connectionsList.updateUI();
        }
    }

    protected void blockTest() {
        this.testBlocked = true;
    }

    protected void unblockTest() {
        this.testBlocked = false;
    }

    protected String getAuthType() {
        if (authType.getSelectedItem() == BASIC || authType.getSelectedItem() == EDIT) {
            return null;
        } else {
            return (String) authType.getSelectedItem();
        }
    }

    protected void setAuthType(String type) {
        authType.setSelectedItem(BASIC);
        if (!StringUtil.isEmptyOrSpaces(type)) {
            authType.setSelectedItem(type);
        }
    }

    protected void testCurrentValues() {
        if (testBlocked) {
            return;
        }
        try {
            new Task.Modal(project, "Check connection", true) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    blockTest();
                    Connection connection = new Connection(textName.getText(), textHost.getText(), Integer.parseInt(textPort.getText()), textUsername.getText(), passwordField1.getPassword(), useEnvCheckbox.isSelected(), textVariable.getText(), multilineCommentSupport.isSelected(), getCurrentBgColor(), getAuthType());
                    List<Connector> connectors = Collections.synchronizedList(new ArrayList<>());
                    succedValidationMessage.setText("");
                    validationMessage.setText("");
                    try {
                        BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
                        Thread executor = new Thread(() -> {
                            try {
                                //QGrid.getInstance(project, false).blockRun();
                                progressIndicator.setText("Establishing connection to the server...");

                                Connector connector = new ConnectorFactory(connection).create();
                                connectors.add(connector);
                                connector.query(new KCharacterVector("key`.q"), KSymbolVector.class, progressIndicator, queue);
                            } catch (Throwable e) {
                                queue.add(e);
                            }
                        });
                        executor.setUncaughtExceptionHandler((t, e) -> {
                            queue.add(e);
                        });
                        Thread check = new Thread(() -> {
                            long timeAfterCancel = -1;
                            while (true) {
                                try {
                                    Object obj = queue.poll(500, TimeUnit.MICROSECONDS);
                                    if (obj == null) {
                                        if (progressIndicator.isCanceled()) {
                                            if (timeAfterCancel < 0) timeAfterCancel = System.currentTimeMillis();
                                            if (connectors.size() > 0) {
                                                connectors.get(0).close();
                                            }
                                            if (System.currentTimeMillis() - timeAfterCancel > 5000) {
                                                return;
                                            }
                                        }
                                    } else {
                                        if (obj instanceof SocketException && progressIndicator.isCanceled()) {
                                            queue.put(new K4Exception("Canceled by user"));
                                        } else {
                                            queue.put(obj);
                                        }
                                        return;
                                    }
                                } catch (InterruptedException e) {
                                    return;
                                }
                            }
                        });

                        executor.start();
                        check.start();
                        executor.join();

                        Object result = queue.poll(1, TimeUnit.SECONDS);
                        if (result == null) {
                            if (check.isAlive()) {
                                check.interrupt();
                            }
                            validationMessage.setText("Execution interrupted by unknown reason.");
                        } else {
                            if (result instanceof KBase) {
                                succedValidationMessage.setText("Valid.");
                            } else {
                                validationMessage.setText(result.toString());
                            }
                        }
                    } catch (Exception e) {
                        validationMessage.setText(e.toString());
                    } finally {
                        if (connectors.size() > 0) {
                            try {
                                connectors.get(0).close();
                            } catch (Exception e) {
                                //IGNORE
                            }
                        }
                    }
                }

                @Override
                public void onFinished() {
                    unblockTest();
                    super.onFinished();
                }
            }.queue();
        } catch (Exception e) {
            validationMessage.setText(e.toString());
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
            textVariable.setText(connection.getPasswordVariable());
            useEnvCheckbox.setSelected(connection.isUsePasswordVariable());
            passwordVisible(!connection.isUsePasswordVariable());
            validationMessage.setText("");
            succedValidationMessage.setText("");
            multilineCommentSupport.setSelected(connection.isMultilineCommentSupport());
            setAuthType(connection.getAuthType());

            try {
                if (connection.getBgColor() != null) {
                    Color color = Color.decode(connection.getBgColor());
                    bgColorEnabled.setSelected(true);
                    bgColor.setSelectedColor(color);
                    bgColor.setEnabled(true);
                } else {
                    setEmptyBgColor();
                }
            } catch (Exception ignore) {
                setEmptyBgColor();
            }
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
        useEnvCheckbox.setSelected(false);
        passwordVisible(true);
        multilineCommentSupport.setSelected(false);
        authType.setSelectedItem(BASIC);
        setEmptyBgColor();
    }

    protected void setEmptyBgColor() {
        bgColorEnabled.setSelected(false);
        bgColor.setSelectedColor(null);
        bgColor.setEnabled(false);

    }

    protected String getCurrentBgColor() {
        if (bgColorEnabled.isSelected()) {
            Color color = bgColor.getSelectedColor();
            if (color != null) {
                return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            }
        }
        return null;
    }

    private void passwordVisible(boolean usePasswordField) {
        labelPassword.setEnabled(usePasswordField);
        passwordField1.setEnabled(usePasswordField);
        labelVariable.setEnabled(!usePasswordField);
        textVariable.setEnabled(!usePasswordField);
    }

    @NotNull
    @Override
    protected Action[] createLeftSideActions() {
        return new Action[]{new ApplyAction(), new TestAction()};
    }

    private void createUIComponents() {
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
