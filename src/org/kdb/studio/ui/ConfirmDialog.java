package org.kdb.studio.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfirmDialog extends DialogWrapper  {
    private JLabel textField;
    private JPanel panel1;

    public ConfirmDialog(@Nullable Project project, String text) {
        super(project);
        setTitle("Confirm overwrite.");
        this.textField.setText(text);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel1;
    }
}
