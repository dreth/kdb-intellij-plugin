package org.kdb.studio.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.kdb.studio.ui.KDBFontsForm;

public class FontConfigManagementAction extends AnAction {

    public FontConfigManagementAction() {
        super("Font Config");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project != null) {
            KDBFontsForm fontsForm = new KDBFontsForm(project);
            fontsForm.show();
        }
    }
}
