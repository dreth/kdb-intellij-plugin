package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.QGrid;

public class ReRunAction extends RunCodeAction {

    public ReRunAction(ConnectionManager connectionManager) {
        super(connectionManager);
        getTemplatePresentation().setText("Refresh");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/refresh.png"));
    }

    @Override
    protected String getQuery(Project project) {
        if (project != null) {
            return QGrid.getInstance(project, false).getTableGroup().getCurrentQuery();
        } else {
            return null;
        }
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isAvailable(e) && QGrid.getInstance(e.getProject(), false).refreshAllowed());
    }

    protected boolean isAvailable(AnActionEvent e) {
        return e.getProject() != null &&  QGrid.getInstance(e.getProject(), false) != null;
    }

}
