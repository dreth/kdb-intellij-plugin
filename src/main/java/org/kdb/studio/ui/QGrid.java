package org.kdb.studio.ui;

import com.google.gson.JsonSyntaxException;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.KDBResourceBundle;
import org.kdb.studio.chart.entity.Plot;
import org.kdb.studio.chart.entity.PlotOverride;
import org.kdb.studio.db.Connection;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.kx.*;
import org.kdb.studio.kx.type.Dict;
import org.kdb.studio.kx.type.Flip;
import org.kdb.studio.kx.type.KBase;
import org.kdb.studio.kx.type.UnaryPrimitive;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class QGrid implements EditorColorsListener {

    public static class State {
        public String query;
        public KBase response;
        public Throwable error;

        public State(String query, KBase response) {
            this.query = query;
            this.response = response;
        }

        public State(Throwable error) {
            this.error = error;
        }
    }

    private JPanel panel1;
    private JTabbedPane tabbedPane1;

    private JTable table;
    private JEditorPane textPane;
    private TableGroup tableGroup;
    private JScrollPane scrollPane;
    private CellRenderer cellRenderer;
    private TableHeaderRenderer tableHeaderRenderer;

    private TableRowHeader trh;

    private WidthAdjuster wa;

    private Project project;

    private String style;

    private AtomicBoolean errorLogged = new AtomicBoolean(false);

    private static Map<Project, QGrid> instanceMap = new WeakHashMap();

    private State state;

    private long executionStart = 0;

    private boolean blocked = false;


    private QGrid() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(EditorColorsManager.TOPIC, this);
        tableGroup = new TableGroup();
        tableGroup.disableAll();
        updateStyles();
    }

    @Override
    public void globalSchemeChange(@Nullable EditorColorsScheme scheme) {
        updateStyles();
    }

    public void updateStyles() {

        ColorAndFontManager colorAndFontManager = ColorAndFontManager.getInstance();
        Font font = colorAndFontManager.getFont(ColorAndFontManager.CONSOLE_FONT);
        textPane.setFont(font);
        textPane.setBackground(colorAndFontManager.getColor(ColorAndFontManager.KDB_CONSOLE_BACKGROUND));
        Color fg = colorAndFontManager.getColor(ColorAndFontManager.KDB_CONSOLE_FOREGROUND);

        StringBuilder fontPrefs = new StringBuilder("style=\"");
        fontPrefs.append("font-family: '").append(font.getFamily()).append("'; ");
        fontPrefs.append("font-size: ").append(font.getSize()).append("pt; ");
        if (font.isItalic()) {
            fontPrefs.append("font-style: italic; ");
        }
        if (font.isBold()) {
            fontPrefs.append("font-weight: bold; ");
        }
        fontPrefs.append("color: ").append(String.format("#%02x%02x%02x", fg.getRed(), fg.getGreen(), fg.getBlue()).toUpperCase()).append(";\"");

        style = fontPrefs.toString();
        textPane.setForeground(fg);
        Insets insets = table.getTableHeader().getInsets();
        table.setRowHeight(getMaxFontHeight(table) - insets.bottom - insets.top);
        cellRenderer.updateStyles();
        trh.updateStyle();
        tableHeaderRenderer.updateStyles();
        wa.resizeAllColumns();
    }

    protected int getMaxFontHeight(JComponent component) {
        ColorAndFontManager manager = ColorAndFontManager.getInstance();
        int[] height = new int[] {
            component.getFontMetrics(manager.getFont(ColorAndFontManager.TABLE_CONTENT_FONT)).getHeight(),
            component.getFontMetrics(manager.getFont(ColorAndFontManager.TABLE_ROW_NUM_FONT)).getHeight()
        };
        int i = Integer.MIN_VALUE;
        for (int h: height) {
            i = Math.max(i, h);
        }
        return i;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void showState() {
        try {
            long exTime = System.currentTimeMillis() - executionStart;
            if (state.error != null) {
                this.showError(state.error);
            } else {
                this.showResponse(state.query, state.response);
            }
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            toolWindowManager.getToolWindow("KDBStudio").setTitle("           Last execution time: " + formatTime(exTime));
        } finally { //unblock the QGrid state
            this.blocked = false;
        }
    }

    protected String formatTime(long ms) {
        if (ms < 1000) {
            return ms + " mS";
        } else if(ms< 60*1000) {
            return new DecimalFormat("#.#").format((double)ms/1000) + " sec";
        } else {
            return (int)(ms/(60 * 1000)) + " min " + (int)(ms % (60* 1000))/1000 + " sec";
        }
    }

    public boolean isBlocked() {
        return blocked;
    }

    public boolean refreshAllowed() {
        return !isBlocked() && getTableGroup().getCurrentQuery() != null;
    }

    public void showTable(String query, KTableModel tableModel) {
        tabbedPane1.setEnabledAt(0, true);
        tabbedPane1.setEnabledAt(1, false);
        tabbedPane1.setSelectedIndex(0);
        tableGroup.enableAll(query, tableModel, table);

        int rows = tableModel.getRowCount();
        this.table.setModel(tableModel);
        errorLogged.set(false);
        this.tabbedPane1.setTitleAt(0, "Table [" + rows + " rows]");
        trh.updateSize();
        wa.resizeAllColumns();
    }

    public JTable getTable() {
        return table;
    }

    public void showConsole(String sb) {
        showConsole(sb, true, null);
    }

    public void showConsole(String sb, boolean asHtml, String query) {
        tabbedPane1.setEnabledAt(0, false);
        tabbedPane1.setEnabledAt(1, true);
        tabbedPane1.setSelectedIndex(1);
        tableGroup.disableAll();
        tableGroup.setCurrentQuery(query);
        this.tabbedPane1.setTitleAt(0, "Table");
        if (asHtml) {
            textPane.setContentType("text/html");
            textPane.setText("<html><body " + style + " >" + sb + "</body></html>");
        } else {
            textPane.setContentType("text/plain");
            textPane.setText(sb);
        }
    }

    public void showResponse(String query, KBase response) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.getToolWindow("KDBStudio").show(null);

        if (FlipTableModel.isTable(response)) {
            KTableModel model;
            if (response instanceof Flip) {
                model = new FlipTableModel(Flip.class.cast(response));
            } else {
                model = new DictTableModel(Dict.class.cast(response));
            }
            showTable(query, model);
        } else {
            LimitedWriter lm = new LimitedWriter(50000);
            try {
                if (!(response instanceof UnaryPrimitive && 0 == ((UnaryPrimitive) response).getPrimitiveAsInt()))
                    response.toString(lm, true);
            } catch (LimitedWriter.LimitException ignore) {
                //Notifications.Bus.notify(new Notification("KDBStudio", "Response is too big to be shown in console", "Data is too long. Cut output.", NotificationType.WARNING));
            } catch (Throwable e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Failed to show response in console", e.getMessage(), NotificationType.WARNING));
            }
            showConsole(lm.toString(), false, query);
        }
    }

    /**
     * Apply plot override from query comment, and return results
     * @param config
     * @return
     */
    public PlotOverride applyPlotConfigOverride(Plot config) {
        String comments = QueryWrapper.toComments(getTableGroup().getCurrentQuery());
        if (!comments.isEmpty()) {
            try {
                Plot plot = JsonParserUtil.loadLastJsonAsPlot(comments);
                if (plot == null) {
                    return new PlotOverride(MessageType.INFO, KDBResourceBundle.message("plot.override.info.message"));
                }
                config.override(plot);
            } catch (JsonSyntaxException e) {
                String err = e.getMessage();
                if (e.getCause() != null) {
                    err = e.getCause().getMessage();
                }
                return new PlotOverride(MessageType.WARNING, KDBResourceBundle.message("plot.override.warn.message", err));
            }
        }
        return new PlotOverride(null, null);
    }

    public void showError(Throwable e) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.getToolWindow("KDBStudio").show(null);
        Connection conn = ConnectionManager.getInstance().getActiveConnection();
        String host = "";
        if (conn != null) {
            host = new StringBuilder(conn.getHost()).append(":").append(conn.getPort()).toString();
        }
        try {
            throw e;
        } catch (IOException ex) {
            showConsole(KDBResourceBundle.message("query.execution.error.message.io", host, ex.getMessage()));
        } catch (K4Exception ex) {
            StringBuilder sb = new StringBuilder();
            String hint = QErrors.lookup(ex.getMessage());

            sb.append(KDBResourceBundle.message("query.execution.error.message.k4", ex.getMessage()));
            if (hint != null) {
                sb.append(KDBResourceBundle.message("query.execution.error.message.k4.hint", hint));
            }
            showConsole(sb.toString());
        } catch (java.lang.OutOfMemoryError ex) {
            showConsole(KDBResourceBundle.message("query.execution.error.message.oom", host));
        } catch (Throwable ex) {

            String message = ex.getMessage();
            if ((message == null) || (message.length() == 0)) {
                message = KDBResourceBundle.message("query.execution.error.message.gen.nomsg", ex.toString());
            }
            showConsole(KDBResourceBundle.message("query.execution.error.message.gen", host, message));
        }

    }

    public void blockRun() {
        this.blocked = true;
        executionStart = System.currentTimeMillis();
    }

    public static QGrid getInstance(Project project, boolean create) {
        QGrid qGrid = project.getService(QGrid.class);
        qGrid.project = project;
        return qGrid;
    }

    public JTabbedPane getTabbedPane1() {
        return tabbedPane1;
    }

    private void createUIComponents() {
        table = new JBTable();
        cellRenderer = new CellRenderer((title, content) -> {
            if (!errorLogged.get()) {
                Notifications.Bus.notify(new Notification("KDBStudio", title, content, NotificationType.WARNING));
                errorLogged.set(true);
            }
        });
        table.setDefaultRenderer(KBase.class, cellRenderer);
        tableHeaderRenderer = new TableHeaderRenderer();
        table.getTableHeader().setDefaultRenderer(tableHeaderRenderer);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane = new JScrollPane(table);
        trh = new TableRowHeader(table);
        scrollPane.setRowHeaderView(trh);

        scrollPane.getRowHeader().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                Point header_pt = ((JViewport) ev.getSource()).getViewPosition();
                Point main_pt = main.getViewPosition();
                if (header_pt.y != main_pt.y) {
                    main_pt.y = header_pt.y;
                    main.setViewPosition(main_pt);
                }
            }

            JViewport main = scrollPane.getViewport();
        });
        wa = new WidthAdjuster(table);
        wa.resizeAllColumns();

        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.getViewport().setBackground(UIManager.getColor("Table.background"));
        JLabel rowCountLabel = new JLabel("");
        rowCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rowCountLabel.setVerticalAlignment(SwingConstants.CENTER);
        rowCountLabel.setOpaque(true);
        rowCountLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        rowCountLabel.setFont(UIManager.getFont("Table.font"));
        rowCountLabel.setBackground(UIManager.getColor("TableHeader.background"));
        rowCountLabel.setForeground(UIManager.getColor("TableHeader.foreground"));
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER,rowCountLabel);

        rowCountLabel = new JLabel("");
        rowCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rowCountLabel.setVerticalAlignment(SwingConstants.CENTER);
        rowCountLabel.setOpaque(true);
        rowCountLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        rowCountLabel.setFont(UIManager.getFont("Table.font"));
        rowCountLabel.setBackground(UIManager.getColor("TableHeader.background"));
        rowCountLabel.setForeground(UIManager.getColor("TableHeader.foreground"));
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER,rowCountLabel);

    }

    public TableGroup getTableGroup() {
        return tableGroup;
    }

    @FunctionalInterface
    public interface ErrorLogger {
        void log(@NotNull String title, @NotNull String content);
    }
}
