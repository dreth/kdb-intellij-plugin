package org.kdb.studio.chart.entity;

import org.jfree.chart.ui.RectangleEdge;

public enum Position {

    TOP(RectangleEdge.TOP), BOTTOM(RectangleEdge.BOTTOM), LEFT(RectangleEdge.LEFT), RIGHT(RectangleEdge.RIGHT);

    private RectangleEdge edge;

    Position(RectangleEdge edge) {
        this.edge = edge;
    }

    public RectangleEdge getEdge() {
        return edge;
    }
}
