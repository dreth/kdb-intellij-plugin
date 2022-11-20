package org.kdb.studio.actions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.db.ConnectionManager;

public class RunAllAction extends RunCodeAction {
    public RunAllAction() {
        super();
        getTemplatePresentation().setText("Run selected or all");
        getTemplatePresentation().setIcon(IconLoader.findIcon("/icons/run_run.png", this.getClass().getClassLoader()));
    }

    @Override
    protected String getQuery(Project project) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            if (selectionModel != null) {
                if (selectionModel.hasSelection()) {
                    return selectionModel.getSelectedText();
                } else {
                    return editor.getDocument().getText();
                }
            }
        }
        return null;
    }
}
