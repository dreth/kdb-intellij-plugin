package org.kdb.studio;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.kdb.studio.ui.QGrid;

public class KDBStudioProjectComponent implements ProjectComponent {

    private Project project;

    public KDBStudioProjectComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(QGrid.getInstance(project, true).getTabbedPane1(), "", false);

        ToolWindow toolWindow = toolWindowManager.registerToolWindow("KDBStudio", false, ToolWindowAnchor.BOTTOM);
        toolWindow.getContentManager().addContent(content);
        toolWindow.setStripeTitle("KDB+");
        toolWindow.setIcon(IconLoader.findIcon("/icons/kx.png"));
        toolWindow.hide(null);

    }

    @Override
    public void projectClosed() {
        QGrid.closeInstance(project);
    }
}
