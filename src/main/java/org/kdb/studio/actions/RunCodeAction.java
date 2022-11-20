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
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.pool2.ObjectPool;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.kx.Connector;
import org.kdb.studio.kx.K4Exception;
import org.kdb.studio.kx.QueryWrapper;
import org.kdb.studio.kx.type.KBase;
import org.kdb.studio.ui.QGrid;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RunCodeAction extends AnAction {

    private ConnectionManager connectionManager;

    public RunCodeAction() {
        super("Run selected or line");
        this.connectionManager = ConnectionManager.getInstance();
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/run.png", this.getClass().getClassLoader()));
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

        try {

            new Task.Backgroundable(project, "Execute query", true) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    ObjectPool<Connector> connectorObjectPool = connectionManager.getActiveConnection().getConnectorPool();
                    List<Connector> connectors = Collections.synchronizedList(new ArrayList<>());
                    try {
                        BlockingQueue<Object> queue = new LinkedBlockingQueue<>();

                        Thread executor = new Thread(() -> {
                            try {
                                QGrid.getInstance(project, false).blockRun();
                                progressIndicator.setText("Establishing connection to the server...");
                                Connector connector = connectorObjectPool.borrowObject();
                                connectors.add(connector);
                                connector.query(QueryWrapper.toRequest(query, connectionManager.getActiveConnection().isMultilineCommentSupport()), KBase.class, progressIndicator, queue);
                            } catch (Throwable e) {
                                queue.add(e);
                            }
                        });
                        executor.setUncaughtExceptionHandler((t, e) -> {
                            queue.add(e);
                        });
                        Thread check = new Thread(() -> {
                            long timeAfterCancel = -1;
                            while (true) {
                                try {
                                    Object obj = queue.poll(500, TimeUnit.MICROSECONDS);
                                    if (obj == null) {
                                        if (progressIndicator.isCanceled()) {
                                            if (timeAfterCancel < 0) timeAfterCancel = System.currentTimeMillis();
                                            if (connectors.size() > 0) {
                                                connectors.get(0).close();
                                            }
                                            if (System.currentTimeMillis() - timeAfterCancel > 5000) {
                                                return;
                                            }
                                        }
                                    } else {
                                        if (obj instanceof SocketException && progressIndicator.isCanceled()) {
                                            queue.put(new K4Exception("Canceled by user"));
                                        } else {
                                            queue.put(obj);
                                        }
                                        return;
                                    }
                                } catch (InterruptedException e) {
                                    return;
                                }
                            }
                        });

                        executor.start();
                        check.start();
                        executor.join();

                        Object result = queue.poll(1, TimeUnit.SECONDS);
                        if (result == null) {
                            if (check.isAlive()) {
                                check.interrupt();
                            }
                            QGrid.getInstance(project, false).setState(new QGrid.State(new IllegalStateException("Execution interrupted by unknown reason.")));
                        } else {
                            if (result instanceof KBase) {
                                QGrid.getInstance(project, false).setState(new QGrid.State(query, KBase.class.cast(result)));
                            } else {
                                QGrid.getInstance(project, false).setState(new QGrid.State(Exception.class.cast(result)));
                            }
                        }
                    } catch (Exception e) {
                        QGrid.getInstance(project, false).setState(new QGrid.State(e));
                    } finally {
                        if (connectors.size() > 0) {
                            try {
                                connectorObjectPool.returnObject(connectors.get(0));
                            } catch (Exception e) {
                                //IGNORE
                            }
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
