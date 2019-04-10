package org.kdb.studio.ui;

import org.kdb.studio.kx.type.Dict;
import org.kdb.studio.kx.type.Flip;
import org.kdb.studio.kx.type.KBaseVector;

public class FlipTableModel extends KTableModel {
    private Flip flip;

    public void append(Flip f) {
        flip.append(f);
        if (isSortedAsc())
            asc(sortedByColumn);
        else if (isSortedDesc())
            desc(sortedByColumn);
    }

    public void asc(int col) {
        sortIndex = null;
        KBaseVector v = (KBaseVector) flip.y.at(col);
        sortIndex = v.gradeUp();
        sorted = 1;
        sortedByColumn = col;
    }

    public void desc(int col) {
        sortIndex = null;
        KBaseVector v = (KBaseVector) flip.y.at(col);

        sortIndex = v.gradeDown();
        sorted = -1;
        sortedByColumn = col;
    }

    public void setData(Flip obj) {
        flip = obj;
    }

    public static boolean isTable(Object obj) {
        if (obj instanceof Flip)
            return true;
        else if (obj instanceof Dict) {
            Dict d = (Dict) obj;

            if ((d.x instanceof Flip) && (d.y instanceof Flip))
                return true;
        }

        return false;
    }

    public FlipTableModel() {
        super();
    }

    public FlipTableModel(Flip obj) {
        super();
        setData(obj);
    }

    public boolean isKey(int column) {
        return false;
    }

    public int getColumnCount() {
        return flip.x.getLength();
    }

    public int getRowCount() {
        return ((KBaseVector) flip.y.at(0)).getLength();
    }

    public Object getValueAt(int row, int col) {
        Object o = null;
        row = (sortIndex == null) ? row : sortIndex[row];
        KBaseVector v = (KBaseVector) flip.y.at(col);
        o = v.at(row);
        return o;
    }

    public String getColumnName(int i) {
        return flip.x.at(i).toString(false);
    }

    public Class getColumnClass(int col) {
        return flip.y.at(col).getClass();
    }

    public KBaseVector getColumn(int col) {
        return (KBaseVector) flip.y.at(col);
    }
}
