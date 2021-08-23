package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.kdb.studio.ui.ColorAndFontManager;

public class EnableFormattingSupportAction extends ToggleAction {

    private ColorAndFontManager fontManager;

    public EnableFormattingSupportAction(ColorAndFontManager fontManager) {
        super("Q language advanced formatting");
        this.fontManager = fontManager;
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
