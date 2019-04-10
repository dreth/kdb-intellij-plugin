package org.kdb.studio;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.kdb.studio.ui.KDBToolbarUIManager;

public class KDBToolbarActionGroup extends DefaultActionGroup {
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(KDBToolbarUIManager.getInstance().isVisible());
    }
}
