package org.kdb.studio.chart.entity;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;

public enum RendererType {

    LINE {
        @Override
        public XYItemRenderer toXYItemRenderer() {
            return new XYLineAndShapeRenderer();
        }
    }, SPLINE {
        @Override
        public XYItemRenderer toXYItemRenderer() {
            return new XYSplineRenderer();
        }
    }, STEP {
        @Override
        public XYItemRenderer toXYItemRenderer() {
            return new XYStepRenderer();
        }
    };

    public abstract XYItemRenderer toXYItemRenderer();
}
