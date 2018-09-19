package org.kdb.studio.ui;

import com.intellij.application.options.colors.AbstractFontOptionsPanel;
import com.intellij.openapi.editor.colors.FontPreferences;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class KDBFontsForm extends DialogWrapper {
    private  Project project;
    private JPanel panel1;
    private JPanel fontEditor;
    private SimpleTree tree;

    public KDBFontsForm(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("KDB+ Studio font preferences");
        init();
        tree.setRootVisible(false);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        ColorAndFontManager colorAndFontManager = ColorAndFontManager.getInstance();
        colorAndFontManager.getFontPreferences().entrySet().forEach(stringMyFontPreferencesEntry -> root.add(new MyTreeNode(stringMyFontPreferencesEntry.getKey(), stringMyFontPreferencesEntry.getValue())));
        tree.setModel(new DefaultTreeModel(root));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.getSelectionModel().addTreeSelectionListener(e -> {
            TreePath path = e.getNewLeadSelectionPath();
            if (path != null) {
                MyTreeNode node = (MyTreeNode) path.getLastPathComponent();
                if (node != null) {
                    MyFontOptionsPanel myFontOptionsPanel = MyFontOptionsPanel.class.cast(fontEditor);
                    myFontOptionsPanel.setFontPreferences(node.getFontPreferences());
                    myFontOptionsPanel.updateOptionsList();
                }
            }
        });
        tree.getSelectionModel().setSelectionPath(new TreePath(new Object[]{root, root.getChildAt(0)}));

    }

    private static class MyTreeNode extends DefaultMutableTreeNode {
        private final String myText;
        private Object myValue;

        public MyTreeNode(String text, FontPreferences settings) {
            myText = text;
            myValue = settings;
            setUserObject(myText);
        }

        public MyFontPreferences getFontPreferences() {
            return MyFontPreferences.class.cast(myValue);
        }

        public String getText() {
            return myText;
        }

        public Object getValue() {
            return myValue;
        }

        public void setValue(Object value) {
            myValue = value;
        }

    }

    private static class MyFontOptionsPanel extends AbstractFontOptionsPanel {

        MyFontPreferences fontPreferences;

        JBCheckBox boldCheckbox;

        JBCheckBox italicCheckbox;

        protected JComponent createControls() {
            JPanel settingPanel = createFontSettingsPanel();
            Component[] components = settingPanel.getComponents();
            components[components.length -1].setVisible(false);
            components[components.length -2].setVisible(false);
            components[components.length -3].setVisible(false);
            components[components.length -4].setVisible(false);
            components[components.length -5].setVisible(false);
            components[components.length -6].setVisible(false);

            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.WEST;
            c.insets = JBUI.insets(BASE_INSET, BASE_INSET, 0, 0);

            c.gridx = 0;
            c.gridy = 5;
            boldCheckbox = new JBCheckBox("Bold");
            boldCheckbox.addActionListener(e -> fontPreferences.setBold(boldCheckbox.isSelected()));
            settingPanel.add(boldCheckbox, c);
            c.gridx = 1;
            italicCheckbox = new JBCheckBox("Italic");
            italicCheckbox.addActionListener(e -> fontPreferences.setItalic(italicCheckbox.isSelected()));
            settingPanel.add(italicCheckbox, c);

            return settingPanel;
        }
        @Override
        protected boolean isReadOnly() {
            return false;
        }

        @Override
        protected boolean isDelegating() {
            return false;
        }


        public void setFontPreferences(MyFontPreferences fontPreferences) {
            this.fontPreferences = fontPreferences;
        }

        @NotNull
        @Override
        protected FontPreferences getFontPreferences() {
            return fontPreferences;
        }

        @Override
        protected void setFontSize(int fontSize) {
            fontPreferences.setSize(fontPreferences.getFontFamily(), fontSize);
        }

        @Override
        protected float getLineSpacing() {
            return fontPreferences.getLineSpacing();
        }

        @Override
        protected void setCurrentLineSpacing(float lineSpacing) {
            this.fontPreferences.setLineSpacing(lineSpacing);
        }

        @Override
        public void updateOptionsList() {
            super.updateOptionsList();
            boldCheckbox.setSelected(fontPreferences.isBold());
            italicCheckbox.setSelected(fontPreferences.isItalic());

        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel1;
    }

    private void createUIComponents() {
        fontEditor = new MyFontOptionsPanel();
    }

    @Override
    protected void doOKAction() {
        ColorAndFontManager colorAndFontManager = ColorAndFontManager.getInstance();
        Map<String, MyFontPreferences> allPreferences = new LinkedHashMap<>();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        for (int i=0; i< root.getChildCount(); i++) {
            MyTreeNode myTreeNode = (MyTreeNode)root.getChildAt(i);
            allPreferences.put(myTreeNode.getText(), myTreeNode.getFontPreferences());
        }
        colorAndFontManager.updateFontPreferences(allPreferences, project);
        super.doOKAction();
    }
}
