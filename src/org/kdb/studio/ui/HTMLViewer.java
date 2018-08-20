package org.kdb.studio.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HTMLViewer extends DialogWrapper {

    private JEditorPane editorPane;

    public HTMLViewer(@Nullable Project project, String content) {
        super(project);
        this.editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setDragEnabled(false);
        editorPane.setContentType("text/html");
        editorPane.setMinimumSize(new java.awt.Dimension(20, 20));
        editorPane.setPreferredSize(new java.awt.Dimension(500, 500));
        if (content.startsWith("\"")) {
            editorPane.setText(content.substring(1, content.length() - 2));
        } else {
            editorPane.setText(content);
        }
        setTitle("Console (as HTML)");
        setModal(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return editorPane;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[0];
    }
}
