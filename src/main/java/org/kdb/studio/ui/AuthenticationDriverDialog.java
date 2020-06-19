package org.kdb.studio.ui;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.db.AuthenticationDriver;
import org.kdb.studio.db.AuthenticationDriverManager;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intellij.icons.AllIcons.Nodes.PpJar;
import static com.intellij.ui.Colors.DARK_GREEN;

public class AuthenticationDriverDialog extends DialogWrapper {
    private JPanel rootPanel;
    private JTextField driverName;
    private JTextField driverClass;
    private JPanel jarPanel;
    private JBList jarsList;
    private JBList driversList;
    private MutableCollectionComboBoxModel driverModel;
    private JLabel testResult;
    private JPanel driversPanel;
    private AuthenticationDriverManager authenticationDriverManager;

    public AuthenticationDriverDialog(@Nullable Project project, @NotNull AuthenticationDriverManager authenticationDriverManager) {
        super(project);
        setTitle("Authentication Drivers Configuration");
        this.authenticationDriverManager = authenticationDriverManager;
        initDialog();
    }

    private void initDialog() {
        init();
        getContentPanel().setPreferredSize(new Dimension(740, 400));
        jarsList.setModel(new CollectionListModel<>());
        jarPanel.add(createJarsListDecorator().createPanel(),
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null));

        jarsList.setCellRenderer(new ColoredListCellRenderer<String>() {
            @Override
            protected void customizeCellRenderer(
                    @NotNull JList<? extends String> list, String value, int index, boolean selected, boolean hasFocus) {
                setIcon(PpJar);
                append(value);
            }
        });
        driversPanel.add(createDriverListDecorator().createPanel(),
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_GROW, null, driversList.getPreferredSize(), null));

        driverModel = new MutableCollectionComboBoxModel();
        driversList.setModel(driverModel);
        updateDriversModel();
        driversList.addListSelectionListener(e -> reset(authenticationDriverManager.getAuthenticationDriverByName((String) driversList.getSelectedValue()).orElseGet(AuthenticationDriver::new)));
    }

    protected void updateDriversModel() {
        driverModel.update(Arrays.stream(authenticationDriverManager.getAuthenticationDrivers()).map(AuthenticationDriver::getName).collect(Collectors.toList()));
        driversList.updateUI();
    }

    @NotNull
    @Override
    protected Action[] createLeftSideActions() {
        return new Action[]{
                new DialogWrapperAction("Apply") {
                    @Override
                    protected void doAction(ActionEvent event) {
                        if (validateInput()) {
                            authenticationDriverManager.addOrUpdate(getAuthenticationDriver());
                            updateDriversModel();
                        }
                    }
                },
                new DialogWrapperAction("Test") {
                    @Override
                    protected void doAction(ActionEvent event) {
                        validateInput();
                    }
                }};
    }

    protected boolean validateInput() {
        if (StringUtil.isEmptyOrSpaces(driverName.getText())) {
            return failMessage("Driver name required.");
        }
        if (ConnectionsManagement.BASIC.equals(driverName.getText()) || ConnectionsManagement.EDIT.equals(driverName.getText())) {
            return failMessage("Driver names '<Basic>' and '<Edit...>' are reserved.");
        }
        if (StringUtil.isEmptyOrSpaces(driverClass.getText())) {
            return failMessage("Driver class required.");
        }
        try {
            getAuthenticationDriver().newAuthenticator().apply("user:password@host:port");
            return successMessage("success");
        } catch (RuntimeException e) {
            return failMessage(e.getMessage());
        }
    }

    protected boolean successMessage(String msg) {
        testResult.setForeground(DARK_GREEN);
        testResult.setText(msg);
        return true;
    }

    protected boolean failMessage(String msg) {
        testResult.setForeground(JBColor.RED);
        testResult.setText(msg);
        return false;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    public AuthenticationDriver getAuthenticationDriver() {
        return new AuthenticationDriver(driverName.getText().trim(), driverClass.getText().trim(), getJarsModel().toList());
    }

    public void reset(AuthenticationDriver driver) {
        driverName.setText(driver.getName());
        driverClass.setText(driver.getClassName());
        getJarsModel().removeAll();
        getJarsModel().addAll(0, driver.getJars());
    }

    private CollectionListModel<String> getJarsModel() {
        return (CollectionListModel<String>) jarsList.getModel();
    }

    @NotNull
    private ToolbarDecorator createJarsListDecorator() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(jarsList);
        decorator.setAddAction(button -> {
            VirtualFile[] choices = FileChooser.chooseFiles(new FileChooserDescriptor(false, false, true, true, false, true),
                    rootPanel, null, null);
            if (choices.length != 0) {
                for (VirtualFile f : choices) {
                    getJarsModel().add(f.getCanonicalPath());
                }
            }
        });
        return decorator;
    }

    @Override
    protected void doOKAction() {
        if (validateInput()) {
            authenticationDriverManager.addOrUpdate(getAuthenticationDriver());
            updateDriversModel();
            super.doOKAction();
        }
    }

    @NotNull
    private ToolbarDecorator createDriverListDecorator() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(driversList);
        decorator.disableUpDownActions();
        decorator.setAddAction(button -> {
            reset(new AuthenticationDriver());
            driversList.clearSelection();
        });
        decorator.setRemoveAction(btn -> {
                    Optional<AuthenticationDriver> authenticationDriver = authenticationDriverManager.getAuthenticationDriverByName((String) driversList.getSelectedValue());
                    authenticationDriver.ifPresent(authenticationDriverManager::remove);
                    updateDriversModel();
                    reset(new AuthenticationDriver());
                    driversList.clearSelection();
                }
        );
        return decorator;
    }

}
