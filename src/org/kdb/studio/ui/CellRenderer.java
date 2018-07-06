package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.kdb.studio.KDBBundle;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.type.KBase;
import org.kdb.studio.kx.type.KBaseVector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;

class CellRenderer extends DefaultTableCellRenderer {

    static Color bgColor = UIManager.getColor("Table.selectionBackground");

    static Color gridColor = UIManager.getColor("Table.background");

    static Color selectionFGColor = UIManager.getColor("Table.selectionForeground");

    static Color fgColor = UIManager.getColor("Table.foreground");

    static Color keyColor = KDBBundle.getKeyColumnBackgroundColor();

    static Color altColor = KDBBundle.getOddColumnBackgroundColor();

    static Color nullColor = KDBBundle.getNullColumnForegroundColor();

    static Font columnFont = KDBBundle.getTableColumnFont();

    private QGrid.ErrorLogger logger;


    public CellRenderer(QGrid.ErrorLogger logger) {
        this.logger = logger;

        setFont(columnFont);
        setBackground(UIManager.getColor("Table.background"));
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
            } catch (IOException e) {
                this.logger.log("Failed to parse data for table view", e.getMessage() != null ? e.getMessage() : e.toString());
            } catch (LimitedWriter.LimitException ex) {
                this.logger.log("Failed to parse data for table view", "Data is loo long. Cut output.");
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
