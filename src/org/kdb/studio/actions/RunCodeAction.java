package org.kdb.studio.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.pool2.ObjectPool;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.kx.Connector;
import org.kdb.studio.kx.type.KBase;
import org.kdb.studio.kx.type.KCharacterVector;
import org.kdb.studio.ui.QGrid;

public class RunCodeAction extends AnAction {

    private ConnectionManager connectionManager;

    public RunCodeAction(ConnectionManager connectionManager) {
        super("Run selected or line");
        this.connectionManager = connectionManager;
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/run.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            String query = getQuery(project);
            if (!StringUtil.isEmptyOrSpaces(query)) {
                executeQuery(query, project);
            } else {
                Notifications.Bus.notify(new Notification("KDBStudio", "Query not found", "Can't find any executable query on selection or current position.", NotificationType.INFORMATION));
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabled(connectionManager.getActiveConnection() != null && project != null && !QGrid.getInstance(project, false).isBlocked() && FileEditorManager.getInstance(project).getSelectedTextEditor() != null);
    }

    protected String getQuery(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (selectionModel != null) {
                if (selectionModel.hasSelection()) {
                    return selectionModel.getSelectedText();
                } else {
                    try {
                        selectionModel.selectLineAtCaret();
                        return selectionModel.getSelectedText();
                    } finally {
                        selectionModel.removeSelection();
                    }
                }
            }
        }
        return null;
    }

    protected void executeQuery(String query, Project project) {
        ObjectPool<Connector> connectorObjectPool = connectionManager.getActiveConnection().getConnectorPool();
        try {
            Connector connector = connectorObjectPool.borrowObject();
            new Task.Backgroundable(project, "Execute query",true) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    try {
                        QGrid.getInstance(project, false).blockRun();
                        KBase response = connector.query(new KCharacterVector(query), KBase.class, ProgressManager.getInstance().getProgressIndicator());
                        QGrid.getInstance(project, false).setState(new QGrid.State(query, response));

                    } catch (Throwable e) {
                        QGrid.getInstance(project, false).setState(new QGrid.State(e));
                    } finally {
                        try {
                            connectorObjectPool.returnObject(connector);
                        } catch (Exception e) {
                            //IGNORE
                        }
                    }
                }

                @Override
                public void onFinished() {
                    QGrid.getInstance(project, false).showState();
                    super.onFinished();
                }
            }.queue();

        } catch (Exception e) {
            connectionManager.getActiveConnection().close();
            QGrid.getInstance(project, false).showError(e);
        }
    }

}
