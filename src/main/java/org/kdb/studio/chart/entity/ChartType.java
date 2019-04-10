package org.kdb.studio.chart.entity;

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;

public enum ChartType {
    LINE(true, false),
    /**
     *
     * Use LINE_MARKER instead
     * Not removed, because of existent saved States may contain it.
     */
    @Deprecated
    STAIRCASE(true, true),
    LINE_MARKER(true, true),
    SCATTER(false, true);

    private boolean lines;
    private boolean shapes;

    ChartType(boolean lines, boolean shapes) {
        this.lines = lines;
        this.shapes = shapes;
    }

    public void apply(XYLineAndShapeRenderer renderer) {
        renderer.setDefaultLinesVisible(lines);
        if (!(renderer instanceof XYStepRenderer)) {
            renderer.setDefaultShapesVisible(shapes);
        }
    }

    public void applySeries(int series, XYLineAndShapeRenderer renderer) {
        if (!(renderer instanceof XYStepRenderer)) {
            renderer.setSeriesLinesVisible(series, lines);
            renderer.setSeriesShapesVisible(series, shapes);
        }
    }


}
