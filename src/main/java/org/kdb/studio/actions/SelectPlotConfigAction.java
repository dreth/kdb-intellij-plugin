package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.ui.LineChartForm;

public class SelectPlotConfigAction extends AnAction {
    private PlotConfigBoxAction plotConfigBoxAction;
    private final LineChartForm lineChartForm;
    private String configId;
    public SelectPlotConfigAction(@Nullable String text, PlotConfigBoxAction plotConfigBoxAction, LineChartForm lineChartForm) {
        super(text);
        this.configId = text;
        this.plotConfigBoxAction = plotConfigBoxAction;
        this.lineChartForm = lineChartForm;
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        plotConfigBoxAction.setActiveConfig(configId);
        lineChartForm.applyConfig(configId);
    }
}
