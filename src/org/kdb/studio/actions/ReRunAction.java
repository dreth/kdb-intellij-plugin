package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.QGrid;
import org.kdb.studio.ui.TableGroupPanel;

public class ReRunAction extends RunCodeAction {

    private TableGroupPanel tableGroupPanel;

    public ReRunAction(TableGroupPanel tableGroupPanel, ConnectionManager connectionManager) {
        super(connectionManager);
        this.tableGroupPanel = tableGroupPanel;
        getTemplatePresentation().setText("Refresh");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/rerun.png"));
    }

    @Override
    protected String getQuery(Project project) {
        return tableGroupPanel.getCurrentQuery();
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(!QGrid.getInstance(e.getProject(), false).isBlocked() &&tableGroupPanel.isActive());
        super.update(e);
    }
}
