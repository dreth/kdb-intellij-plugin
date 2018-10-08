package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.ui.WindowWrapperBuilder;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.LineChartForm;

import javax.swing.*;

public class ChartAction extends QGridAction {

    public ChartAction() {
        super("Show chart");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/chart.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (isAvailable(anActionEvent) && getTableGroup(anActionEvent).getCurrentTableModel() != null) {

            LineChartForm lineChartForm = new LineChartForm(getTableGroup(anActionEvent).getCurrentTableModel());
            WindowWrapper wrapper = new WindowWrapperBuilder(WindowWrapper.Mode.FRAME, lineChartForm.createCenterPanel())
                    .setProject(project).setTitle("Chart").build();
            wrapper.getWindow().setSize(wrapper.getComponent().getPreferredSize());
            wrapper.getWindow().setLocationRelativeTo(null);
            wrapper.show();
            lineChartForm.addPreferredSizeChangeListener(dimension -> {
                if (JFrame.class.isAssignableFrom(wrapper.getWindow().getClass())) {
                    if (JFrame.class.cast(wrapper.getWindow()).getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                        wrapper.getWindow().setSize(dimension);
                    }
                }
            });
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isAvailable(e) && getTableGroup(e).isActive() && getTableGroup(e).isShowChart());
        super.update(e);
    }
}
