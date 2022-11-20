package org.kdb.studio;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import org.kdb.studio.ui.KDBToolbarUIManager;

public class KDBToolbarActionGroup extends DefaultActionGroup {
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(
            ApplicationManager.getApplication().getService(KDBToolbarUIManager.class).isVisible());
    }
}
