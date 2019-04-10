package org.kdb.studio.chart.entity;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public enum MarkerType {
    SQUARE {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            return new Rectangle2D.Double(-delta, -delta, size, size);
        }
    },
    CIRCLE {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            return new Ellipse2D.Double(-delta, -delta, size, size);
        }
    },
    ELLIPSE {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            return new Ellipse2D.Double(-delta, -delta / 2, size, size / 2);
        }
    },
    TRIANGLE_UP_POINTING {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            int[] xpoints = intArray(0.0, delta, -delta);
            int[] ypoints = intArray(-delta, delta, delta);
            return new Polygon(xpoints, ypoints, 3);
        }
    },
    TRIANGLE_DOWN_POINTING {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            int[] xpoints = intArray(-delta, +delta, 0.0);
            int[] ypoints = intArray(-delta, -delta, delta);
            return new Polygon(xpoints, ypoints, 3);
        }
    },
    TRIANGLE_RIGHT_POINTING {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            int[] xpoints = intArray(-delta, delta, -delta);
            int[] ypoints = intArray(-delta, 0.0, delta);
            return new Polygon(xpoints, ypoints, 3);
        }
    },
    TRIANGLE_LEFT_POINTING {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            int[] xpoints = intArray(-delta, delta, delta);
            int[] ypoints = intArray(0.0, -delta, +delta);
            return new Polygon(xpoints, ypoints, 3);
        }
    },
    DIAMOND {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            int[] xpoints = intArray(0.0, delta, 0.0, -delta);
            int[] ypoints = intArray(-delta, 0.0, delta, 0.0);
            return new Polygon(xpoints, ypoints, 4);
        }
    },
    RECTANGLE_HORIZONTAL {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            return new Rectangle2D.Double(-delta, -delta / 2, size, size / 2);
        }
    },
    RECTANGLE_VERTICAL {
        @Override
        public Shape toShape(int size) {
            double delta = size / 2.0;
            return new Rectangle2D.Double(-delta / 2, -delta, size / 2, size);
        }
    };
    public abstract Shape toShape(int size);

    private static int[] intArray(double a, double b, double c) {
        return new int[] {(int) a, (int) b, (int) c};
    }

    private static int[] intArray(double a, double b, double c, double d) {
        return new int[] {(int) a, (int) b, (int) c, (int) d};
    }
}
