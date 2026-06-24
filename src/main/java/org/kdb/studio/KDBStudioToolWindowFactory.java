package org.kdb.studio;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.ui.QGrid;

public class KDBStudioToolWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    ContentFactory contentFactory = ContentFactory.getInstance();
    Content content = contentFactory.createContent(QGrid.getInstance(project, true).getTabbedPane1(), "", false);

    toolWindow.getContentManager().addContent(content);
    toolWindow.setStripeTitle("KDB+");
    toolWindow.setIcon(IconLoader.findIcon("/icons/kx.png", this.getClass().getClassLoader()));
    toolWindow.hide(null);
  }
}
