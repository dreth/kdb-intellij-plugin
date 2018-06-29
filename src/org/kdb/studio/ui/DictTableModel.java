package org.kdb.studio.ui;

import org.kdb.studio.kx.type.Dict;
import org.kdb.studio.kx.type.Flip;
import org.kdb.studio.kx.type.KBaseVector;
import org.kdb.studio.kx.type.KSymbolVector;

public class DictTableModel extends KTableModel {
    private Dict dict;

    public void setData(Dict obj) {
        dict = obj;
    }

    public DictTableModel() {
    }


    public void upsert(Dict upd) {
        setData(upd);
//        dict.upsert(upd);
        if (isSortedAsc()) {
            asc(sortedByColumn);
        } else if (isSortedDesc()) {
            desc(sortedByColumn);
        }
    }

    public DictTableModel(Dict obj) {
        setData(obj);
    }

    public boolean isKey(int column) {
        Flip f = (Flip) dict.x;

        if (column < f.x.getLength())
            return true;
        return false;
    }

    public void asc(int col) {
        sortIndex = null;
        sortedByColumn = col;

        Flip f = (Flip) dict.x;
        KBaseVector v = null;

        if (col >= f.x.getLength()) {
            col -= f.x.getLength();
            f = (Flip) dict.y;
        }
        v = (KBaseVector) f.y.at(col);
        sortIndex = v.gradeUp();
        sorted = 1;
    }

    public void desc(int col) {
        sortIndex = null;
        sortedByColumn = col;

        Flip f = (Flip) dict.x;
        KBaseVector v = null;

        if (col >= f.x.getLength()) {
            col -= f.x.getLength();
            f = (Flip) dict.y;
        }
        v = (KBaseVector) f.y.at(col);
        sortIndex = v.gradeDown();
        sorted = -1;
    }

    public int getColumnCount() {
        return ((Flip) dict.x).x.getLength() + ((Flip) dict.y).x.getLength();
    }

    public int getRowCount() {
        return ((KBaseVector) ((Flip) dict.x).y.at(0)).getLength();
    }

    public Object getValueAt(int row, int col) {
        Object o = null;
        row = (sortIndex == null) ? row : sortIndex[row];
        Flip f = (Flip) dict.x;
        KBaseVector v = null;

        if (col >= f.x.getLength()) {
            col -= f.x.getLength();
            f = (Flip) dict.y;
        }

        v = (KBaseVector) f.y.at(col);
        o = v.at(row);

        //   if( o instanceof K.KBaseVector)
        // {
        //   o=K.decode((K.KBase)o);
        //  }

        return o;
    }

    public String getColumnName(int col) {
        KSymbolVector v = ((Flip) dict.x).x;

        if (col >= ((Flip) dict.x).x.getLength()) {
            col -= ((Flip) dict.x).x.getLength();
            v = ((Flip) dict.y).x;
        }
        return v.at(col).toString(false);
    }

    public Class getColumnClass(int col) {
        Flip f = (Flip) dict.x;
        KBaseVector v = null;

        if (col >= f.x.getLength()) {
            col -= f.x.getLength();
            f = (Flip) dict.y;
        }

        v = (KBaseVector) f.y.at(col);

        return v.getClass();
    }

    public KBaseVector getColumn(int col) {
        Flip f = (Flip) dict.x;
        KBaseVector v = null;

        if (col >= f.x.getLength()) {
            col -= f.x.getLength();
            f = (Flip) dict.y;
        }

        return (KBaseVector) f.y.at(col);
    }
}
