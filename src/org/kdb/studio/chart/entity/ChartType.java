package org.kdb.studio.chart.entity;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public enum ChartType {
    LINE(true, false), STAIRCASE(false, true), SCATTER(true, true);

    private boolean lines;
    private boolean shapes;

    ChartType(boolean lines, boolean shapes) {
        this.lines = lines;
        this.shapes = shapes;
    }

    public void apply(XYLineAndShapeRenderer renderer) {
        renderer.setDefaultLinesVisible(lines);
        renderer.setDefaultShapesVisible(shapes);
    }


}
