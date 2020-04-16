package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.kdb.studio.actions.PlotConfigBoxAction;
import org.kdb.studio.actions.PlotOverrideAction;
import org.kdb.studio.chart.ChartConfigurator;
import org.kdb.studio.chart.PlotConfigManager;
import org.kdb.studio.chart.entity.Plot;
import org.kdb.studio.chart.entity.PlotOverride;
import org.kdb.studio.kx.ToDouble;
import org.kdb.studio.kx.type.*;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class LineChartForm {
    private Project project;
    private JComponent plotConfig;
    private ChartPanel chartPanel;
    private JPanel centralPanel;
    private KTableModel table;
    private JFreeChart chart;
    private PlotConfigBoxAction plotConfigBoxAction;
    private PlotOverrideAction plotOverrideAction;
    private List<PreferredSizeChangeListener> listeners = new ArrayList<>();

    private static ZonedDateTime kdbEpochStart = ZonedDateTime.of(2001, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

    public LineChartForm(KTableModel table, Project project) {
        this.table = table;
        this.project = project;
        String configId = PlotConfigManager.getInstance().forModel(table).getId();
        plotConfigBoxAction.setActiveConfig(configId);
        applyConfig(configId);
        chartPanel.setMouseZoomable(true, false);
    }

    public JComponent createCenterPanel() {
        return centralPanel;
    }

    public static RegularTimePeriod convertToPeriod(KBase period, Class kBaseVector, TimeZone tz) {
        if (KDateVector.class == kBaseVector) {
            KDate date = (KDate) period;
            return new Day(Date.from(date.toDate()), tz, Locale.getDefault());
        } else if (KTimeVector.class == kBaseVector) {
            KTime time = (KTime) period;
            return new Millisecond(Date.from(time.toTime()), tz, Locale.getDefault());
        } else if (KTimestampVector.class == kBaseVector) {
            KTimestamp date = (KTimestamp) period;
            return new Day(Date.from(Instant.class.cast(date.toTimestamp()[0])), tz, Locale.getDefault());
        } else if (KTimespanVector.class == kBaseVector) {
            KTimespan time = (KTimespan) period;
            return new Millisecond(time.toTime(), tz, Locale.getDefault());
        } else if (KDatetimeVector.class == kBaseVector) {
            KDatetime time = (KDatetime) period;
            return new Millisecond(Date.from(time.toTimestamp()), tz, Locale.getDefault());
        } else if (KMonthVector.class == kBaseVector) {
            org.kdb.studio.kx.type.Month time = (org.kdb.studio.kx.type.Month) period;
            int m = time.i + 24000;
            int y = m / 12;
            m = 1 + m % 12;
            return new org.jfree.data.time.Month(m, y);
        } else if (KSecondVector.class == kBaseVector) {
            org.kdb.studio.kx.type.Second time = (org.kdb.studio.kx.type.Second) period;
            ZonedDateTime dateTime = kdbEpochStart.plus(time.i, ChronoUnit.SECONDS);
            return new org.jfree.data.time.Second(dateTime.getSecond(), dateTime.getMinute(), dateTime.getHour(), dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear());
        } else if (KMinuteVector.class == kBaseVector) {
            org.kdb.studio.kx.type.Minute time = (org.kdb.studio.kx.type.Minute) period;
            ZonedDateTime dateTime = kdbEpochStart.plus(time.i, ChronoUnit.MINUTES);
            return new org.jfree.data.time.Minute(dateTime.getMinute(), dateTime.getHour(), dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear());
        }
        return null;
    }

    public static JFreeChart createDataset(KTableModel table) {
        TimeZone tz = TimeZone.getTimeZone("GMT");

        XYDataset ds = null;

        if (table.getColumnCount() > 0) {
            Class klass = table.getColumnClass(0);

            if (Arrays.asList(KTimestampVector.class, KTimespanVector.class, KDateVector.class, KTimeVector.class, KMonthVector.class, KMinuteVector.class, KSecondVector.class, KDatetimeVector.class).contains(klass)) {
                TimeSeriesCollection tsc = new TimeSeriesCollection(tz);
                for (int col = 1; col < table.getColumnCount(); col++) {
                    TimeSeries series = null;
                    try {
                        series = new TimeSeries(table.getColumnName(col));
                        KBaseVector periodColumn = table.getColumn(0);
                        for (int row = 0; row < periodColumn.getLength(); row++) {
                            KBase period = periodColumn.at(row);
                            Object o = table.getValueAt(row, col);
                            if (o instanceof KBase && !((KBase) o).isNull() && o instanceof ToDouble) {
                                series.addOrUpdate(convertToPeriod(period, klass, tz), ((ToDouble) o).toDouble());
                            }
                        }
                    } catch (SeriesException e) {
                        Notifications.Bus.notify(new Notification("KDBStudio", "Failed to parse data for chart view", e.getMessage(),  NotificationType.WARNING));
                    }
                    if (series.getItemCount() > 0)
                        tsc.addSeries(series);
                }
                ds = tsc;
            } else if (Arrays.asList(KDoubleVector.class, KFloatVector.class, KShortVector.class, KIntVector.class, KLongVector.class).contains(klass)) {
                XYSeriesCollection xysc = new XYSeriesCollection();

                for (int col = 1; col < table.getColumnCount(); col++) {
                    XYSeries series = null;

                    try {
                        series = new XYSeries(table.getColumnName(col));

                        for (int row = 0; row < table.getRowCount(); row++) {
                            double x = ((ToDouble) table.getValueAt(row, 0)).toDouble();
                            Object y = table.getValueAt(row, col);
                            if (y instanceof ToDouble) {
                                series.addOrUpdate(x, ((ToDouble) y).toDouble());
                            }
                        }
                    } catch (SeriesException e) {
                        Notifications.Bus.notify(new Notification("KDBStudio", "Failed to parse data for chart view", e.getMessage(),  NotificationType.WARNING));
                    }

                    if (series.getItemCount() > 0)
                        xysc.addSeries(series);
                }

                ds = xysc;
            }
        }

        if (ds != null) {
            boolean legend = false;

            if (ds.getSeriesCount() > 1)
                legend = true;
            //ChartFactory doesn't allows for set timezone from method parameters.
            //Set required as default, and set it back after init;
            TimeZone defultTZ = TimeZone.getDefault();
            TimeZone.setDefault(tz);
            try {
                if (ds instanceof XYSeriesCollection) {
                    return ChartFactory.createXYLineChart("",
                            "",
                            "",
                            ds,
                            PlotOrientation.VERTICAL,
                            legend,
                            true,
                            true);
                } else if (ds instanceof TimeSeriesCollection) {
                    return ChartFactory.createTimeSeriesChart("",
                            "",
                            "",
                            ds,
                            legend,
                            true,
                            true);
                }
            } finally {
                //restore TimeZone
                TimeZone.setDefault(defultTZ);
            }
        }

        return null;
    }

    private void createUIComponents() {
        plotConfigBoxAction = new PlotConfigBoxAction(PlotConfigManager.getInstance(), this);
        plotOverrideAction = new PlotOverrideAction();
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(plotOverrideAction);
        actionGroup.add(plotConfigBoxAction);
        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar toolbar = actionManager.createActionToolbar("KDBStudio.LineChartForm", actionGroup, true);

        plotConfig = toolbar.getComponent();

        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    }

    @FunctionalInterface
    public interface PreferredSizeChangeListener {
        void sizeChanged(Dimension dimension);
    }

    public void addPreferredSizeChangeListener(PreferredSizeChangeListener listener) {
        listeners.add(listener);
    }

    public void applyConfig(String configId) {
        Plot config = null;
        chart = createDataset(table);
        if (chart != null) {
            try {
                config = PlotConfigManager.getInstance().byName(configId);
                PlotOverride override = QGrid.getInstance(project, false).applyPlotConfigOverride(config);
                plotOverrideAction.setPlotOverride(override);
                new ChartConfigurator().configureChart(config, chart);
            } catch (Exception e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Apply chart config error.", e.toString(), NotificationType.ERROR));
            }
            if (config != null && config.getSize() != null) {
                chartPanel.setPreferredSize(new Dimension(config.getSize().getWidth(), config.getSize().getHeight()));
                if (centralPanel != null) {
                    centralPanel.setPreferredSize(new Dimension(config.getSize().getWidth(), config.getSize().getHeight() + plotConfig.getHeight()));
                    listeners.stream().forEach(listener -> listener.sizeChanged(centralPanel.getPreferredSize()));
                }
            }
        }
        chartPanel.setChart(chart);
    }
}
