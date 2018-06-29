package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.FileExporter;
import org.kdb.studio.ui.TableGroupPanel;

public class ExportAction extends AnAction {

    private TableGroupPanel tableGroupPanel;

    private FileExporter fileExporter;

    public ExportAction(TableGroupPanel tableGroupPanel) {
        super("Export");
        fileExporter = new FileExporter();
        this.tableGroupPanel = tableGroupPanel;
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/export1.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        fileExporter.exportTable(anActionEvent.getProject(), tableGroupPanel.getTable());
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(tableGroupPanel.isActive());
        super.update(e);
    }
}
