package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.LineChart;
import org.kdb.studio.ui.LineChartForm;

public class ChartAction extends QGridAction {

    public ChartAction() {
        super("Show chart");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/chart.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (isAvailable(anActionEvent) && getTableGroup(anActionEvent).getCurrentTableModel() != null) {
            new LineChartForm(project, getTableGroup(anActionEvent).getCurrentTableModel()).show();
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isAvailable(e) && getTableGroup(e).isActive() && getTableGroup(e).isShowChart());
        super.update(e);
    }
}
