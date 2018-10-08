package org.kdb.studio.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.kdb.studio.chart.entity.*;
import org.kdb.studio.chart.entity.Label;

import java.awt.*;
import java.awt.Font;
import java.util.Optional;

public class ChartConfigurator {

    public void configureChart(Plot plot, JFreeChart chart) {
        applyLegend(chart, plot.getLegend());
        applyTitle(chart, plot.getTitle());
        applyGrid(chart, plot.getGrid());
        applyAxis(chart, plot.getDomainAxis(), plot.getRangeAxis());

        if (plot.getPadding() != null) {
            chart.setPadding(new RectangleInsets(plot.getPadding().getTop(), plot.getPadding().getLeft(), plot.getPadding().getBottom(), plot.getPadding().getRight()));
        }
        if (plot.getBackgroundColor() != null) {
            chart.setBackgroundPaint(toPaint(plot.getBackgroundColor()));
        }
        if (plot.getPlotBackgroundColor() != null) {
            chart.getPlot().setBackgroundPaint(toPaint(plot.getPlotBackgroundColor()));
        }
        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        if (renderer instanceof XYLineAndShapeRenderer) {
            Optional.ofNullable(plot.getType()).orElse(ChartType.LINE).apply(XYLineAndShapeRenderer.class.cast(renderer));
            XYLineAndShapeRenderer.class.cast(renderer).setUseFillPaint(true);
            XYLineAndShapeRenderer.class.cast(renderer).setUseOutlinePaint(true);
        }
        int i=0;
        for (Series series: plot.getSeries()) {
            renderer.setSeriesStroke(i, Optional.ofNullable(series.getLineType()).orElse(LineType.SOLID).toStroke(series.getLineWidth()));
            if (series.getShow() != null) {
                renderer.setSeriesVisible(i, series.getShow(), false);
            }
            if (series.getColor() != null) {
                renderer.setSeriesPaint(i, toPaint(series.getColor()), false);
            }
            if (series.getFillColor() != null) {
                renderer.setSeriesFillPaint(i, toPaint(series.getFillColor()), false);
            }
            if (series.getOutlineColor() != null) {
                renderer.setSeriesOutlinePaint(i, toPaint(series.getOutlineColor()), false);
            }
            if (series.getVisibleInLegend() != null) {
                renderer.setSeriesVisibleInLegend(i, series.getVisibleInLegend(), false);
            }
            if (series.getMarker() != null) {
                renderer.setSeriesShape(i, series.getMarker().getType().toShape(series.getMarker().getSize()), false);
            }
            i++;
        }
        int totalSeries = chart.getXYPlot().getSeriesCount();
        for (i=0; i< totalSeries; i++) {
            Paint paint = AbstractRenderer.class.cast(renderer).lookupSeriesPaint(i);
            if (renderer.getSeriesFillPaint(i) == null) {
                renderer.setSeriesFillPaint(i, paint, false);
            }
            if (renderer.getSeriesOutlinePaint(i) == null) {
                renderer.setSeriesOutlinePaint(i, paint, false);
            }
        }
    }

    private void applyLegend(JFreeChart chart, Legend leg) {
        chart.clearSubtitles();
        if (leg != null && leg.isShow()) {
            LegendTitle legend = new LegendTitle(chart.getPlot());
            if (leg.getMargin() != null) {
                legend.setMargin(new RectangleInsets(leg.getMargin().getTop(), leg.getMargin().getLeft(), leg.getMargin().getBottom(), leg.getMargin().getRight()));
            }
            if (leg.getBackgroundColor() != null) {
                legend.setBackgroundPaint(toPaint(leg.getBackgroundColor()));
            }
            if (leg.getPosition() != null) {
                legend.setPosition(leg.getPosition().getEdge());
            }
            if (leg.getFont() != null) {
                legend.setItemFont(toFont(leg.getFont()));
            }
            chart.addLegend(legend);
        }

    }

    private void applyTitle(JFreeChart chart, Label title) {
        if (title != null && title.isShow()) {
            TextTitle textTitle = chart.getTitle();
            if (textTitle == null) {
                textTitle = new TextTitle();
                chart.setTitle(textTitle);
            }
            textTitle.setText(title.getText());
            if (title.getFont() != null) {
                textTitle.setFont(toFont(title.getFont()));
            }
            if (title.getColor() != null) {
                textTitle.setPaint(toPaint(title.getColor()));
            }
        }
        else {
            chart.setTitle((TextTitle) null);
        }
    }

    private void applyGrid(JFreeChart chart, Grid grid) {
        if (XYPlot.class.isAssignableFrom(chart.getPlot().getClass()) && grid != null) {
            XYPlot xyPlot = XYPlot.class.cast(chart.getPlot());
            xyPlot.setDomainGridlinesVisible(grid.isVertical());
            xyPlot.setRangeGridlinesVisible(grid.isHorizontal());
            if (grid.getVerticalLineColor() != null) {
                xyPlot.setDomainGridlinePaint(toPaint(grid.getVerticalLineColor()));
            }
            if (grid.getHorizontalLineColor() != null) {
                xyPlot.setRangeGridlinePaint(toPaint(grid.getHorizontalLineColor()));
            }
            xyPlot.setRangeGridlineStroke(Optional.ofNullable(grid.getHorizontalLineType()).orElse(LineType.DASHED).toStroke(grid.getHorizontalLineWidth()));
            xyPlot.setDomainGridlineStroke(Optional.ofNullable(grid.getVerticalLineType()).orElse(LineType.DASHED).toStroke(grid.getVerticalLineWidth()));

        }
    }

    private void applyAxis(Axis axis, DomainAxis domainAxis) {
        if (axis != null && domainAxis != null) {
            if (domainAxis.getLabel() != null && domainAxis.getLabel().isShow()) {
                Label label = domainAxis.getLabel();
                axis.setLabel(label.getText());
                if (label.getFont() != null) {
                    axis.setLabelFont(toFont(label.getFont()));
                }
                if (label.getColor() != null) {
                    axis.setLabelPaint(toPaint(label.getColor()));
                }
            } else {
                axis.setLabel(null);
            }
            Tick tick = domainAxis.getTicks();
            if (tick != null) {
                axis.setTickMarksVisible(tick.isShowTickMarks());
                axis.setTickLabelsVisible(tick.isShowTickLabels());
                if (tick.getTickLabelFont() != null) {
                    axis.setTickLabelFont(toFont(tick.getTickLabelFont()));
                }
                if (tick.getTickLabelColor() != null) {
                    axis.setTickLabelPaint(toPaint(tick.getTickLabelColor()));
                }
            }
            if (domainAxis.getAxisLineColor() != null) {
                axis.setAxisLinePaint(toPaint(domainAxis.getAxisLineColor()));
            }
            if (domainAxis.getTickMarkColor() != null) {
                axis.setTickMarkPaint(toPaint(domainAxis.getTickMarkColor()));
            }
        }
    }

    private void applyAxis(JFreeChart chart, DomainAxis domainAxis, RangeAxis rangeAxis) {
        org.jfree.chart.plot.Plot plot = chart.getPlot();
        org.jfree.chart.axis.Axis axis = null;
        org.jfree.chart.axis.ValueAxis valueAxis = null;
        if (plot instanceof CategoryPlot) {
            CategoryPlot p = (CategoryPlot) plot;
            axis = p.getDomainAxis();
            valueAxis = p.getRangeAxis();
        }
        else if (plot instanceof XYPlot) {
            XYPlot p = (XYPlot) plot;
            axis = p.getDomainAxis();
            valueAxis = p.getRangeAxis();
        }
        applyAxis(axis, domainAxis);
        if (valueAxis != null && rangeAxis != null) {
            applyAxis(valueAxis, rangeAxis);
            Range range = rangeAxis.getRange();
            if (range != null) {
                valueAxis.setAutoRange(range.isAutoAdjust());
                if (!range.isAutoAdjust()) {
                    valueAxis.setRange(range.getMinimumRangeValue(), range.getMaximumRangeValue());
                }
            }
            TickUnit tickUnit = rangeAxis.getTickUnit();
            if (tickUnit != null) {
                valueAxis.setAutoTickUnitSelection(tickUnit.isAutoSelection());
                if (!tickUnit.isAutoSelection() && axis instanceof NumberAxis) {
                    ((NumberAxis) axis).setTickUnit(new NumberTickUnit(tickUnit.getTickUnitValue()));
                }
            }

        }
    }

    private Paint toPaint(String model) {
        return Color.decode(model);
    }

    private Font toFont(org.kdb.studio.chart.entity.Font model) {
        int style = 0;
        for (FontAttributes fontAttributes: model.getAttributes()) {
            style = style | fontAttributes.ordinal();
        }
        return new Font(model.getFont(), style, model.getSize());
    }
}
