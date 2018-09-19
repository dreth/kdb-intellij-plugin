package org.kdb.studio.ui;

import com.intellij.json.JsonFileType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.chart.ChartConfigLoader;
import org.kdb.studio.chart.JsonSchemaValidator;
import org.kdb.studio.chart.PlotConfigManager;
import org.kdb.studio.chart.PlotDefaultType;
import org.kdb.studio.chart.entity.Plot;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlotConfigManagement extends DialogWrapper {

    private JTable plotTable;
    private JPanel panel;

    private Project project;

    public PlotConfigManagement(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("Plot configurations");
        init();

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    private void createUIComponents() {
        plotTable = new JBTable();
        final String[] columns = {"Plot id", "Default for"};
        PlotConfigManager configManager = PlotConfigManager.getInstance();
        plotTable.setModel(new AbstractTableModel() {

            @Override
            public String getColumnName(int column) {
                return columns[column];
            }

            @Override
            public int getRowCount() {
                return configManager.listAllPlots(false).size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                String id = configManager.listAllPlots().get(rowIndex);
                if (columnIndex == 0) {
                    return id;
                }
                if (columnIndex == 1) {
                    PlotDefaultType type = configManager.getState().typeFor(id);
                    return type != null ? type.toString() : "";
                }
                return null;
            }

            public boolean isCellEditable(int row, int col) {
                if (col == 1) {
                    return true;
                } else {
                    return false;
                }
            }

            public void setValueAt(Object value, int row, int col) {

                if (col == 1) {
                    String id = configManager.listAllPlots(false).get(row);
                    if ("".equals(value)) {
                        configManager.getState().setDefaultId(null, id);
                    } else {
                        configManager.getState().setDefaultId(PlotDefaultType.valueOf(value.toString()), id);
                    }
                }
                fireTableDataChanged();

            }
        });
        setUpDefaultColumn(plotTable.getColumnModel().getColumn(1));
    }

    private void setUpDefaultColumn(TableColumn defaultColumn) {
        JComboBox comboBox = new ComboBox();
        comboBox.addItem("");
        Arrays.stream(PlotDefaultType.values()).map(Enum::name).forEach(comboBox::addItem);
        defaultColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    @Override
    protected void createDefaultActions() {
        super.createDefaultActions();
        this.myOKAction.putValue("Name", "Exit");
        this.myHelpAction.setEnabled(false);
    }

    @NotNull
    protected Action[] createActions() {
        return new Action[]{this.getOKAction()};
    }

    @NotNull
    @Override
    protected Action[] createLeftSideActions() {
        return new Action[]{new RemoveSelected(), new AddNew()};
    }

    protected class RemoveSelected extends DialogWrapper.DialogWrapperAction {

        private RemoveSelected() {
            super("Remove selected");
        }

        protected void doAction(ActionEvent e) {
            int[] rowsToDelete = plotTable.getSelectedRows();
            if (rowsToDelete.length > 0) {
                ConfirmDialog dialog = new ConfirmDialog(project, "Remove selected configurations?", "Confirm remove.");
                dialog.show();
                if (DialogWrapper.OK_EXIT_CODE == dialog.getExitCode()) {
                    List<String> ids = Arrays.stream(rowsToDelete).mapToObj(row -> plotTable.getValueAt(row, 0).toString()).collect(Collectors.toList());
                    PlotConfigManager configManager = PlotConfigManager.getInstance();
                    for (String id : ids) {
                        configManager.getState().remove(id);
                    }
                    plotTable.updateUI();
                }
            }

        }
    }

    protected class AddNew extends DialogWrapper.DialogWrapperAction {
        private AddNew() {
            super("Add new");
        }

        protected void doAction(ActionEvent event) {
            VirtualFile[] files = FileChooser.chooseFiles(FileChooserDescriptorFactory.createSingleFileDescriptor(JsonFileType.INSTANCE), project, null);
            if (files.length == 1) {
                //first validate input content by schema
                try {
                    List<String> problems = JsonSchemaValidator.validate(files[0].getInputStream());
                    if (!problems.isEmpty()) {
                        Messages.showIdeaMessageDialog(project, problems.stream().collect(Collectors.joining("\n")), "Invalid incoming data format", new String[] {Messages.OK_BUTTON}, 0, null, null );
                        return;
                    }

                } catch (IOException e) {
                    Messages.showIdeaMessageDialog(project, Optional.ofNullable(e.getMessage()).orElse(e.toString()), "Data import error.", new String[] {Messages.OK_BUTTON}, 0, null, null );
                    return;
                }
                try (InputStream is = files[0].getInputStream()) {
                    Plot plot = ChartConfigLoader.load(is);
                    PlotConfigManager configManager = PlotConfigManager.getInstance();
                    if (PlotConfigManager.DEFAULT_ID.equals(plot.getId())) {
                        Messages.showIdeaMessageDialog(project, "Plot id <<DEFAULT>> is not allowed (internally reserved)", "Invalid incoming data.", new String[] {Messages.OK_BUTTON}, 0, null, null );
                        return;
                    }
                    if (configManager.listAllPlots(false).contains(plot.getId())) {
                        ConfirmDialog dialog = new ConfirmDialog(project, "Plot configuration with id "+ plot.getId() + " already exists. Overwrite?", "Confirm overwrite");
                        dialog.show();
                        if (DialogWrapper.OK_EXIT_CODE == dialog.getExitCode()) {
                            addPlotConfig(plot);
                        }
                    } else {
                        addPlotConfig(plot);
                    }

                } catch (Exception e) {
                    Notifications.Bus.notify(new Notification("KDBStudio", "Data import error.", e.getMessage(), NotificationType.ERROR));
                }
            }
        }

        protected void addPlotConfig(Plot plot) {
            PlotConfigManager.getInstance().getState().getPlots().add(plot);
            plotTable.updateUI();
        }
    }
}
