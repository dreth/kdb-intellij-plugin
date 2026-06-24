package org.kdb.studio.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.ui.FlipTableModel;
import org.kdb.studio.ui.QGrid;

import javax.swing.*;
import java.io.*;

public class ShowAsHTMLAction extends AnAction {

    public ShowAsHTMLAction() {
        super("Open console output in default browser");
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            QGrid qGrid = QGrid.getInstance(project, false);
            if (qGrid != null && !qGrid.isBlocked()) {
                QGrid.State state = qGrid.getState();
                if (state != null && state.error == null && !FlipTableModel.isTable(state.response)) {
                    try {
                        File file = FileUtil.createTempFile("kdb-plugin-console-view", ".html", true);
                        try (FileOutputStream os = new FileOutputStream(file); Writer writer = new OutputStreamWriter(os)) {
                            state.response.toString(new DelegatedLimitedWriter(writer), false);
                        }
                        BrowserUtil.browse(file);
                    } catch (IOException e) {
                        Notifications.Bus.notify(new Notification("KDBStudio", "Failed to open output as HTML", e.getMessage(), NotificationType.WARNING));
                    }
                }
            }
        }
    }

    static class DelegatedLimitedWriter extends LimitedWriter {
        private Writer writer;

        public DelegatedLimitedWriter(Writer writer) {
            super(-1);
            this.writer = writer;
        }

        @Override
        public void write(char c) throws IOException {
            writer.write(c);
        }

        @Override
        public void write(String s) throws IOException {
            writer.write(s);
        }
    }
}
