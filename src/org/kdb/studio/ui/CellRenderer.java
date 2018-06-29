package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
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

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        if (value instanceof KBase) {
            KBase kb = (KBase) value;
            LimitedWriter w = new LimitedWriter(256);

            try {
                kb.toString(w, kb instanceof KBaseVector);
            } catch (IOException e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Failed to parse data for table view", e.getMessage(),  NotificationType.WARNING));
            } catch (LimitedWriter.LimitException ex) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Failed to parse data for table view", ex.getMessage(),  NotificationType.WARNING));
            }
            setText(w.toString());
        }
        if (isSelected) {
            setBackground(bgColor);
        } else {
            setBackground(gridColor);
        }
        return this;
    }
}
