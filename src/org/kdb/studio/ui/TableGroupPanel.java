package org.kdb.studio.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.kdb.studio.actions.ChartAction;
import org.kdb.studio.actions.ExcelAction;
import org.kdb.studio.actions.ExportAction;
import org.kdb.studio.actions.ReRunAction;
import org.kdb.studio.db.ConnectionManager;

import javax.swing.*;
import java.awt.*;

public class TableGroupPanel extends JPanel {

    boolean active = false;

    boolean showChart = false;

    private String currentQuery;

    private KTableModel currentTableModel;

    private JTable table;

    private ConnectionManager connectionManager;

    public boolean isActive() {
        return active;
    }

    private DefaultActionGroup actionGroup;

    public TableGroupPanel() {
        this.connectionManager = ConnectionManager.getInstance();
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
    }

    private JComponent createToolBar() {
        actionGroup = new DefaultActionGroup();
        actionGroup.add(new ExcelAction(this));
        actionGroup.add(new ExportAction(this));
        actionGroup.add(new ChartAction(this));
        actionGroup.addSeparator();
        actionGroup.add(new ReRunAction(this, connectionManager));
        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar toolbar = actionManager.createActionToolbar("KDBStudio.TableGroupPanel", actionGroup, true);

        return toolbar.getComponent();
    }

    public void disableAll() {
        active = false;
    }

    public void enableAll(String query, KTableModel tableModel, JTable table) {
        this.currentQuery = query;
        this.currentTableModel = tableModel;
        active = true;
        this.table = table;
        showChart = LineChart.couldBeShown(tableModel);
    }

    public String getCurrentQuery() {
        return currentQuery;
    }

    public JTable getTable() {
        return table;
    }

    public KTableModel getCurrentTableModel() {
        return currentTableModel;
    }

    public boolean isShowChart() {
        return showChart;
    }
}
