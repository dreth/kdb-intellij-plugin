package org.kdb.studio.ui;

import javax.swing.*;
import java.awt.*;

public class BlankIcon implements Icon {
    private Icon i;

    public BlankIcon(Icon i) {
        this.i = i;
    }

    public void paintIcon(Component component,Graphics g,int i,int i0) {
    }

    public int getIconWidth() {
        return i.getIconWidth();
    }

    public int getIconHeight() {
        return i.getIconHeight();
    }
};
