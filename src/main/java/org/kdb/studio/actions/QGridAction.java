package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.ui.QGrid;
import org.kdb.studio.ui.TableGroup;

public abstract class QGridAction extends AnAction {

    public QGridAction(@Nullable String text) {
        super(text);
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    protected boolean isAvailable(AnActionEvent e) {
        return e.getProject() != null &&  QGrid.getInstance(e.getProject(), false) != null;
    }

    protected TableGroup getTableGroup(AnActionEvent e) {
        return QGrid.getInstance(e.getProject(), false).getTableGroup();
    }
}
