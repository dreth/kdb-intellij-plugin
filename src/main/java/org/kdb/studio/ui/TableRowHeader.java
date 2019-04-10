package org.kdb.studio.ui;

import com.intellij.ui.SideBorder;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class TableRowHeader extends JList {
    private JTable table;
    private RowHeaderRenderer rowHeaderRenderer;

    public void recalcWidth() {
        Insets i = new RowHeaderRenderer().getInsets();
        int w = i.left + i.right;
        int width = SwingUtilities.computeStringWidth(table.getFontMetrics(getFont()),
                (table.getRowCount() < 9999 ? "9999" : "" + (table.getRowCount() - 1)));
        // used to be rowcount - 1 as 0 based index
        setFixedCellWidth(w + width);
    }

    public TableRowHeader(final JTable table) {
        this.table = table;

        setAutoscrolls(false);
        rowHeaderRenderer = new RowHeaderRenderer();
        setCellRenderer(rowHeaderRenderer);
        setFixedCellHeight(table.getRowHeight());
        recalcWidth();
        setFocusable(false);
        updateStyle();
        setModel(new TableListModel());
        setOpaque(false);
        setSelectionModel(table.getSelectionModel());
        MouseInputAdapter mia = new MouseInputAdapter() {
            int startIndex = 0;

            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                startIndex = index;
                table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
                table.setRowSelectionInterval(index, index);
                table.requestFocus();
            }

            public void mouseReleased(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
                table.setRowSelectionInterval(startIndex, index);
                table.requestFocus();
            }

            public void mouseDragged(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
                table.setRowSelectionInterval(startIndex, index);
                table.requestFocus();
            }
        };
        addMouseListener(mia);
        addMouseMotionListener(mia);
    }

    public void updateSize() {
        setFixedCellHeight(table.getRowHeight());
        recalcWidth();
    }

    public void updateStyle() {
        setFont(ColorAndFontManager.getInstance().getFont(ColorAndFontManager.TABLE_ROW_NUM_FONT));
        rowHeaderRenderer.updateStyle();
    }

    class TableListModel extends AbstractListModel {
        public int getSize() {
            return table.getRowCount();
        }

        public Object getElementAt(int index) {
            return String.valueOf(index);
        }
    }

    class RowHeaderRenderer extends JLabel implements ListCellRenderer {
        RowHeaderRenderer() {
            super();
            setHorizontalAlignment(RIGHT);
            setVerticalAlignment(CENTER);
            setOpaque(true);
            setBorder(new SideBorder(UIManager.getColor("Table.gridColor"), SideBorder.RIGHT | SideBorder.BOTTOM | SideBorder.TOP));
            setBackground(UIManager.getColor("TableHeader.background"));
            setForeground(UIManager.getColor("TableHeader.foreground"));
            //setFont(UIManager.getFont("Table.font"));
            this.updateStyle();
        }

        public void updateStyle() {
            setFont(ColorAndFontManager.getInstance().getFont(ColorAndFontManager.TABLE_ROW_NUM_FONT));
            setFixedCellHeight(table.getRowHeight());
        }



        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (index > 0) {
                setBorder(new SideBorder(UIManager.getColor("Table.gridColor"), SideBorder.RIGHT | SideBorder.BOTTOM ));
            } else {
                setBorder(new SideBorder(UIManager.getColor("Table.gridColor"), SideBorder.RIGHT | SideBorder.BOTTOM | SideBorder.TOP));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}