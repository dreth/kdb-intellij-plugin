package org.kdb.studio.chart.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Plot {
    public String id;
    public ChartType type;
    public Label title;
    public DomainAxis domainAxis;
    public RangeAxis rangeAxis;
    public Grid grid;
    public String backgroundColor;
    public String plotBackgroundColor;
    public Legend legend;
    public List<Series> series;
    public Size size;

    public Plot() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plot plot = (Plot) o;
        return Objects.equals(id, plot.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public DomainAxis getDomainAxis() {
        return domainAxis;
    }

    public void setDomainAxis(DomainAxis domainAxis) {
        this.domainAxis = domainAxis;
    }

    public RangeAxis getRangeAxis() {
        return rangeAxis;
    }

    public void setRangeAxis(RangeAxis rangeAxis) {
        this.rangeAxis = rangeAxis;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Label getTitle() {
        return title;
    }

    public void setTitle(Label title) {
        this.title = title;
    }

    public ChartType getType() {
        return type;
    }

    public void setType(ChartType type) {
        this.type = type;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getPlotBackgroundColor() {
        return plotBackgroundColor;
    }

    public void setPlotBackgroundColor(String plotBackgroundColor) {
        this.plotBackgroundColor = plotBackgroundColor;
    }

    public Legend getLegend() {
        return legend;
    }

    public void setLegend(Legend legend) {
        this.legend = legend;
    }

    public List<Series> getSeries() {
        if (series == null) {
            series = new ArrayList<>();
        }
        return series;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
