package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.LineChart;
import org.kdb.studio.ui.TableGroupPanel;

public class ChartAction extends AnAction {

    private TableGroupPanel tableGroupPanel;

    public ChartAction(TableGroupPanel tableGroupPanel) {
        super("Show chart");
        this.tableGroupPanel = tableGroupPanel;
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/chart.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null && tableGroupPanel.getCurrentTableModel() != null) {
            new LineChart(project, tableGroupPanel.getCurrentTableModel()).show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(tableGroupPanel.isActive() && tableGroupPanel.isShowChart());
        super.update(e);
    }
}
