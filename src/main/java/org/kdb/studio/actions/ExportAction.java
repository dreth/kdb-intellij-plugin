package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.ui.FileExporter;

public class ExportAction extends QGridAction {

    private FileExporter fileExporter;

    public ExportAction() {
        super("Export");
        fileExporter = new FileExporter();
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/export1.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (!isAvailable(anActionEvent)) {
            return;
        }
        fileExporter.exportTable(anActionEvent.getProject(), getTableGroup(anActionEvent).getTable());
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isAvailable(e) && getTableGroup(e).isActive());
        super.update(e);
    }
}
