package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.chart.PlotConfigManager;
import org.kdb.studio.ui.LineChartForm;

import javax.swing.*;

public class PlotConfigBoxAction extends ComboBoxAction {

    private PlotConfigManager plotConfigManager;

    private LineChartForm lineChartForm;

    private String activeConfig;

    public PlotConfigBoxAction(PlotConfigManager plotConfigManager, LineChartForm lineChartForm) {
        this.plotConfigManager = plotConfigManager;
        this.lineChartForm = lineChartForm;
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        Presentation presentation = e.getPresentation();
        presentation.setText(activeConfig);
    }

    public void setActiveConfig(String activeConfig) {
        this.activeConfig = activeConfig;
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        for (String plot : plotConfigManager.listAllPlots()) {
            actionGroup.add(new SelectPlotConfigAction(plot, this, lineChartForm));
        }
        actionGroup.addSeparator();
        actionGroup.add(new PlotConfigManagementAction("Plot configuration...", IconLoader.findIcon("/icons/editSource.png", this.getClass().getClassLoader())));
        return actionGroup;
    }
}
