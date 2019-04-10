package org.kdb.studio.ui;

import javax.swing.*;

public class TableGroup {

    boolean active = false;

    boolean showChart = false;

    private String currentQuery;

    private KTableModel currentTableModel;

    private JTable table;

    public boolean isActive() {
        return active;
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

    public void setCurrentQuery(String currentQuery) {
        this.currentQuery = currentQuery;
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
