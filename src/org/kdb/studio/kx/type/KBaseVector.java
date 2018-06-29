package org.kdb.studio.kx.type;

import org.kdb.studio.kx.Sorter;

import java.lang.reflect.Array;

public abstract class KBaseVector extends KBase {
    protected static final String enlist = "enlist ";
    protected Object array;
    private int length;

    protected KBaseVector(Class klass, int length) {
        //array=Array.newInstance(klass, calcCapacity(length));
        array = Array.newInstance(klass, length);
        this.length = length;
    }

    public abstract KBase at(int i);

    public int getLength() {
        return length;
    }

    public Object getArray() {
        return array;
    }

    public int[] gradeUp() {
        return Sorter.gradeUp(getArray(), getLength());
    }

    public int[] gradeDown() {
        return Sorter.gradeDown(getArray(), getLength());
    }

    protected int calcCapacity(int length) {
        return (int) (1.1 * length);
    }

    public void append(KBaseVector x) {
        if ((x.getLength() + getLength()) > Array.getLength(getArray())) {
            int newLength = Array.getLength(getArray()) + x.getLength();
            Object tmp = Array.newInstance(getArray().getClass().getComponentType(), 2 * calcCapacity(newLength));
            System.arraycopy(getArray(), 0, tmp, 0, getLength());
            array = tmp;
        }
        System.arraycopy(x.getArray(), 0, getArray(), getLength(), x.getLength());
        length += x.getLength();
    }
}