package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.kdb.studio.ui.ColorAndFontManager;

public class EnableFormattingSupportAction extends ToggleAction {

    private ColorAndFontManager fontManager;

    public EnableFormattingSupportAction() {
        super("Q language advanced formatting");
        this.fontManager = ColorAndFontManager.getInstance();
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

     @Override
    public boolean isSelected(AnActionEvent e) {
        return fontManager.getFormattingEnabled();
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        fontManager.setFormattingEnabled(state);
    }
}
