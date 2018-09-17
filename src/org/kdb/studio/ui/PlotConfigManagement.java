package org.kdb.studio.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.chart.PlotConfigManager;
import org.kdb.studio.chart.PlotDefaultType;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Optional;

public class PlotConfigManagement extends DialogWrapper {

    private JTable plotTable;

    public PlotConfigManagement(@Nullable Project project) {
        super(project);
        init();

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return plotTable;
    }

    private void createUIComponents() {
        plotTable = new JBTable();
        PlotConfigManager configManager = PlotConfigManager.getInstance();
        plotTable.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return configManager.listAllPlots().size();
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
                    String id = configManager.listAllPlots().get(row);
                    if ("".equals(value)) {
                        configManager.getState().setDefaultId(null, id);
                    } else {
                        configManager.getState().setDefaultId(PlotDefaultType.valueOf(value.toString()), id);
                    }
                }
                fireTableCellUpdated(row, col);

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

    @NotNull
    @Override
    protected Action[] createLeftSideActions() {
        return new Action[]{new RemoveSelected(plotTable), new AddNew()};
    }

    protected class RemoveSelected extends DialogWrapper.DialogWrapperAction {

        private JTable plotTable;

        private RemoveSelected(JTable plotTable) {
            super("Remove Selected");
            this.plotTable = plotTable;
        }

        protected void doAction(ActionEvent e) {
        }
    }

    protected class AddNew extends DialogWrapper.DialogWrapperAction {
        private AddNew() {
            super("Add New");
        }

        protected void doAction(ActionEvent e) {
        }
    }
}
