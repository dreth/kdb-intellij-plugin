package org.kdb.studio.ui;

import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.type.KBase;
import org.kdb.studio.kx.type.KBaseVector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;

class CellRenderer extends DefaultTableCellRenderer {

    private Color bgColor;

    private Color gridColor;

    private Color selectionFGColor;

    private Color fgColor;

    private Color keyColor;

    private Color altColor;

    private Color nullColor;

    private QGrid.ErrorLogger logger;

    public CellRenderer(QGrid.ErrorLogger logger) {
        this.logger = logger;
        updateStyles();
    }

    public void updateStyles() {
        EditorColorsScheme editorColorsScheme = EditorColorsManager.getInstance().getSchemeForCurrentUITheme();
        setFont(editorColorsScheme.getFont(EditorFontType.PLAIN));

        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        keyColor = scheme.getColor(KDBColorSettingsPage.KDB_KEY_COLUMN_BACKGROUND);
        altColor = scheme.getColor(KDBColorSettingsPage.KDB_ODD_COLUMN_BACKGROUND);
        nullColor = scheme.getColor(KDBColorSettingsPage.KDB_NULL_COLUMN_FOREGROUND);
        bgColor = scheme.getColor(KDBColorSettingsPage.KDB_TABLE_SELECTION_BACKGROUND);
        selectionFGColor = scheme.getColor(KDBColorSettingsPage.KDB_TABLE_SELECTION_FOREGROUND);
        gridColor = scheme.getColor(KDBColorSettingsPage.KDB_TABLE_BACKGROUND);
        fgColor = scheme.getColor(KDBColorSettingsPage.KDB_TABLE_FOREGROUND);
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        if (value instanceof KBase) {
            KBase kb = (KBase) value;
            LimitedWriter w = new LimitedWriter(512);

            try {
                kb.toString(w, kb instanceof KBaseVector);
            } catch (LimitedWriter.LimitException ex) {
                this.logger.log("Failed to parse data for table view", "Data is loo long. Cut output.");
            } catch (Throwable e) {
                this.logger.log("Failed to parse data for table view", e.getMessage() != null ? e.getMessage() : e.toString());
            }
            setText(w.toString());
            setForeground(kb.isNull() ? nullColor : fgColor);
        }

        if (!isSelected) {
            KTableModel ktm = (KTableModel) table.getModel();
            column = table.convertColumnIndexToModel(column);
            if (ktm.isKey(column))
                setBackground(keyColor);
            else if (row % 2 == 0)
                setBackground(altColor);
            else
                setBackground(gridColor);
        } else {
            setForeground(selectionFGColor);
            setBackground(bgColor);
        }
        setHorizontalAlignment(SwingConstants.RIGHT);
        return this;
    }
}
