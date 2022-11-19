package org.kdb.studio.ui;

import com.intellij.openapi.util.IconLoader;
import org.kdb.studio.kx.type.KSymbolVector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TableHeaderRenderer extends DefaultTableCellRenderer {
    public TableHeaderRenderer() {
        setHorizontalAlignment(SwingConstants.RIGHT);
        setVerticalAlignment(SwingConstants.CENTER);
        updateStyles();
    }

    public void updateStyles() {
        setFont(ColorAndFontManager.getInstance().getFont(ColorAndFontManager.TABLE_HEADER_FONT));
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        if (table.getModel() instanceof KTableModel) {
            column = table.convertColumnIndexToModel(column);
            Icon icon = null;

            Insets insets = getInsets();
            int targetHeight = getFontMetrics(getFont()).getHeight() - insets.bottom - insets.top;
            KTableModel ktm = (KTableModel) table.getModel();
            if (ktm.isSortedDesc()) {
                if (column == ktm.getSortByColumn())
                    if (ktm.getColumnClass(column) == KSymbolVector.class)
                        icon = new ScaledIcon(IconLoader.findIcon("/icons/sort_az_ascending.png", this.getClass().getClassLoader()), targetHeight);
                    else
                        icon = new ScaledIcon(IconLoader.findIcon("/icons/sort_descending.png", this.getClass().getClassLoader()), targetHeight);
            } else if (ktm.isSortedAsc())
                if (column == ktm.getSortByColumn())
                    if (ktm.getColumnClass(column) == KSymbolVector.class)
                        icon = new ScaledIcon(IconLoader.findIcon("/icons/sort_az_descending.png", this.getClass().getClassLoader()), targetHeight);
                    else
                        icon = new ScaledIcon(IconLoader.findIcon("/icons/sort_ascending.png", this.getClass().getClassLoader()), targetHeight);
            if (icon != null)
                setIcon(icon);
            else {
                icon = new ScaledIcon(IconLoader.findIcon("/icons/sort_ascending.png", this.getClass().getClassLoader()), targetHeight);
                setIcon(new BlankIcon(icon));
            }
        }

        String text = " ";
        if (value != null)
            text = value.toString() + " ";

        setText(text);

        return this;
    }
}