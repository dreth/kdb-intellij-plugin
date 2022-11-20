package org.kdb.studio.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.ui.ExcelExporter;

import java.io.File;
import java.io.IOException;

public class ExcelAction extends QGridAction {

    public ExcelAction() {
        super("Open in Excel");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/excel_icon.png", this.getClass().getClassLoader()));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        if (!isAvailable(anActionEvent)) {
            return;
        }
        try {
            File file = File.createTempFile("Export",".xls");
            ProgressManager.getInstance().run(new Task.Backgroundable(anActionEvent.getProject(), "Exporting data to " + file.getAbsolutePath(), true) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    try {
                        new ExcelExporter().exportTableX(progressIndicator, getTableGroup(anActionEvent).getTable(), file, true);
                    } catch (IOException e) {
                        Notifications.Bus.notify(new Notification("KDBStudio", "There was an error converting to excel.", e.getMessage(), NotificationType.ERROR));
                    }
                }
            });
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification("KDBStudio", "There was an error converting to excel.", e.getMessage(), NotificationType.ERROR));
        }


    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isAvailable(e) && getTableGroup(e).isActive());
        super.update(e);
    }
}
