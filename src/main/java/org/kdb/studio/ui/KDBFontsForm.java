package org.kdb.studio.ui;

import com.intellij.openapi.editor.colors.FontPreferences;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class KDBFontsForm extends DialogWrapper {
    private Project project;
    private JPanel panel1;
    private JPanel fontEditor;
    private JTree tree;

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

    private static class MyFontOptionsPanel extends JPanel {

        private MyFontPreferences fontPreferences;
        private final JComboBox<String> fontFamilyCombo;
        private final JSpinner fontSizeSpinner;
        private final JSpinner lineSpacingSpinner;
        private final JBCheckBox boldCheckbox;
        private final JBCheckBox italicCheckbox;
        private boolean updating;

        MyFontOptionsPanel() {
            super(new GridBagLayout());
            String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            Arrays.sort(fontFamilies, String.CASE_INSENSITIVE_ORDER);
            fontFamilyCombo = new JComboBox<>(fontFamilies);
            fontSizeSpinner = new JSpinner(new SpinnerNumberModel(FontPreferences.DEFAULT_FONT_SIZE, 6, 96, 1));
            lineSpacingSpinner = new JSpinner(new SpinnerNumberModel((double) FontPreferences.DEFAULT_LINE_SPACING, 0.6d, 3.0d, 0.05d));
            boldCheckbox = new JBCheckBox("Bold");
            italicCheckbox = new JBCheckBox("Italic");

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(4, 4, 4, 4);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;

            addRow("Font", fontFamilyCombo, c, 0);
            addRow("Size", fontSizeSpinner, c, 1);
            addRow("Line spacing", lineSpacingSpinner, c, 2);

            c.gridx = 1;
            c.gridy = 3;
            c.weightx = 1;
            JPanel stylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            stylePanel.add(boldCheckbox);
            stylePanel.add(italicCheckbox);
            add(stylePanel, c);

            c.gridx = 0;
            c.gridy = 4;
            c.gridwidth = 2;
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;
            add(Box.createVerticalGlue(), c);

            ActionListener actionListener = e -> applyToPreferences();
            ChangeListener changeListener = e -> applyToPreferences();
            fontFamilyCombo.addActionListener(actionListener);
            boldCheckbox.addActionListener(actionListener);
            italicCheckbox.addActionListener(actionListener);
            fontSizeSpinner.addChangeListener(changeListener);
            lineSpacingSpinner.addChangeListener(changeListener);
        }

        private void addRow(String label, JComponent component, GridBagConstraints c, int row) {
            c.gridx = 0;
            c.gridy = row;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            add(new JLabel(label), c);

            c.gridx = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(component, c);
        }

        public void setFontPreferences(MyFontPreferences fontPreferences) {
            this.fontPreferences = fontPreferences;
        }

        public void updateOptionsList() {
            if (fontPreferences == null) {
                return;
            }
            updating = true;
            String family = fontPreferences.getFontFamily();
            fontFamilyCombo.setSelectedItem(family);
            if (!family.equals(fontFamilyCombo.getSelectedItem())) {
                fontFamilyCombo.addItem(family);
                fontFamilyCombo.setSelectedItem(family);
            }
            fontSizeSpinner.setValue(fontPreferences.getSize(family));
            lineSpacingSpinner.setValue((double) fontPreferences.getLineSpacing());
            boldCheckbox.setSelected(fontPreferences.isBold());
            italicCheckbox.setSelected(fontPreferences.isItalic());
            updating = false;
        }

        private void applyToPreferences() {
            if (updating || fontPreferences == null) {
                return;
            }
            String family = String.valueOf(fontFamilyCombo.getSelectedItem());
            int size = ((Number) fontSizeSpinner.getValue()).intValue();
            float lineSpacing = ((Number) lineSpacingSpinner.getValue()).floatValue();

            fontPreferences.clearFonts();
            fontPreferences.register(family, size);
            fontPreferences.setLineSpacing(lineSpacing);
            fontPreferences.setBold(boldCheckbox.isSelected());
            fontPreferences.setItalic(italicCheckbox.isSelected());
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
