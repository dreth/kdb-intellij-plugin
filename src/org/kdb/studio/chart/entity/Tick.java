package org.kdb.studio.chart.entity;

public class Tick {
    public boolean showTickLabels;
    public boolean showTickMarks;
    public Font tickLabelFont;
    public String tickLabelColor;

    public String getTickLabelColor() {
        return tickLabelColor;
    }

    public void setTickLabelColor(String tickLabelColor) {
        this.tickLabelColor = tickLabelColor;
    }

    public boolean isShowTickLabels() {
        return showTickLabels;
    }

    public void setShowTickLabels(boolean showTickLabels) {
        this.showTickLabels = showTickLabels;
    }

    public boolean isShowTickMarks() {
        return showTickMarks;
    }

    public void setShowTickMarks(boolean showTickMarks) {
        this.showTickMarks = showTickMarks;
    }

    public Font getTickLabelFont() {
        return tickLabelFont;
    }

    public void setTickLabelFont(Font tickLabelFont) {
        this.tickLabelFont = tickLabelFont;
    }
}
