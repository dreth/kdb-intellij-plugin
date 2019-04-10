package org.kdb.studio.chart.entity;

import java.awt.*;

public enum LineType {

    SOLID(null), DASHED(new float[] {2f, 2f}), DASH_DOT(new float[] {2.0f, 2.0f, 1.0f, 2.0f}), DOT_DOT(new float[] {1.0f, 4.0f});

    private float[] dash;

    LineType(float[] dash) {
        this.dash = dash;
    }

    public BasicStroke toStroke(float width) {
        if (dash != null) {
            float[] arr = new float[dash.length];
            for (int i=0; i<dash.length; i++) {
                arr[i] = width * dash[i];
            }
            return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, arr, 0.0f);
        } else {
            return new BasicStroke(width);
        }
    }
}
