package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.CollectionComboBoxModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.kdb.studio.chart.ChartConfigurator;
import org.kdb.studio.chart.PlotConfigManager;
import org.kdb.studio.chart.entity.Plot;
import org.kdb.studio.kx.ToDouble;
import org.kdb.studio.kx.type.*;
import org.kdb.studio.kx.type.Minute;
import org.kdb.studio.kx.type.Month;
import org.kdb.studio.kx.type.Second;

import javax.swing.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LineChartForm extends DialogWrapper {
    private JComboBox plotConfig;
    private ChartPanel chartPanel;
    private JPanel centralPanel;
    private KTableModel table;
    private JFreeChart chart;

    public LineChartForm(@Nullable Project project, KTableModel table) {
        super(project);
        this.table = table;
        setModal(false);
        setTitle("Chart");
        plotConfig.setModel(new CollectionComboBoxModel(PlotConfigManager.getInstance().listAllPlots()));
        plotConfig.addItemListener(e -> {
            String item = (String) e.getItem();
            Plot config = null;
            chart = createDataset(table);
            if (chart != null) {
                try {
                    config = PlotConfigManager.getInstance().byName(item);
                    new ChartConfigurator().configureChart(config, chart);
                } catch (Exception ignore) {
                    //
                }
                if (config != null && config.getSize() != null) {
                    chartPanel.setPreferredSize(new java.awt.Dimension(config.getSize().getWidth(), config.getSize().getHeight()));
                }
            }
            chartPanel.setChart(chart);
        });

        init();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[0];
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return centralPanel;
    }

    public static boolean couldBeShown(KTableModel table) {
        if (table != null && table.getColumnCount() > 1) {
            Class klass = table.getColumnClass(0);
            return Arrays.asList(KTimestampVector.class, KTimespanVector.class, KDateVector.class, KTimeVector.class, KMonthVector.class, KMinuteVector.class, KSecondVector.class, KDatetimeVector.class, KDoubleVector.class, KFloatVector.class, KShortVector.class, KIntVector.class, KLongVector.class).contains(klass);
        }
        return false;
    }

    public static Class<? extends RegularTimePeriod> typeOf(Class kBaseVector) {
        if (KDateVector.class == kBaseVector) {
            return Day.class;
        } else if (KTimeVector.class == kBaseVector) {
            return Millisecond.class;
        } else if (KTimestampVector.class == kBaseVector) {
            return Day.class;
        } else if (KTimespanVector.class == kBaseVector) {
            return Millisecond.class;
        } else if (KDatetimeVector.class == kBaseVector) {
            return Millisecond.class;
        } else if (KMonthVector.class == kBaseVector) {
            return org.jfree.data.time.Month.class;
        } else if (KSecondVector.class == kBaseVector) {
            return org.jfree.data.time.Second.class;
        } else if (KMinuteVector.class == kBaseVector) {
            return org.jfree.data.time.Minute.class;
        }
        return null;
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
            return new org.jfree.data.time.Second(time.i % 60, time.i / 60, 0, 1, 1, 2001);
        } else if (KMinuteVector.class == kBaseVector) {
            org.kdb.studio.kx.type.Minute time = (org.kdb.studio.kx.type.Minute) period;
            return new org.jfree.data.time.Minute(time.i % 60, time.i / 60, 1, 1, 2001);
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

        Plot config = null;
        chart = createDataset(table);
        if (chart != null) {
            try {
                config = PlotConfigManager.getInstance().forModel(table);
                new ChartConfigurator().configureChart(config, chart);
            } catch (Exception ignore) {
//
            }
            chartPanel = new ChartPanel(chart);
            if (config != null && config.getSize() != null) {
                chartPanel.setPreferredSize(new java.awt.Dimension(config.getSize().getWidth(), config.getSize().getHeight()));
            } else {
                chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
            }
            chartPanel.setMouseZoomable(true, false);
        }
    }
}
