package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.db.Connection;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.kx.K4Exception;
import org.kdb.studio.kx.LimitedWriter;
import org.kdb.studio.kx.QErrors;
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


    private QGrid(Project project) {
        this.project = project;
        tableGroup = new TableGroup();
        tableGroup.disableAll();
        updateStyles();
    }

    @Override
    public void globalSchemeChange(@Nullable EditorColorsScheme scheme) {
        updateStyles();
        cellRenderer.updateStyles();
        trh.updateStyle();
        tableHeaderRenderer.updateStyles();
    }

    protected void updateStyles() {
        EditorColorsScheme editorColorsScheme = EditorColorsManager.getInstance().getSchemeForCurrentUITheme();
        Font font = editorColorsScheme.getFont(EditorFontType.PLAIN);
        textPane.setFont(font);
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        textPane.setBackground(scheme.getColor(KDBColorSettingsPage.KDB_CONSOLE_BACKGROUND));
        Color fg = scheme.getColor(KDBColorSettingsPage.KDB_CONSOLE_FOREGROUND);
        style = new StringBuilder("style=\"font-family: '").append(font.getFamily()).append("'; font-size:").append(font.getSize()).append("pt; color:")
                .append(String.format("#%02x%02x%02x", fg.getRed(), fg.getGreen(), fg.getBlue()).toUpperCase()).append(";\"").toString();
        textPane.setForeground(scheme.getColor(KDBColorSettingsPage.KDB_CONSOLE_FOREGROUND));
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

    public void showError(Throwable e) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.getToolWindow("KDBStudio").show(null);
        Connection conn = ConnectionManager.getInstance().getActiveConnection();

        try {
            throw e;
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder("<span style=\"color:red\">");
            sb.append("A communications error occurred while sending the query ");
            if (conn != null) {
                sb.append(" to ").append(conn.getHost()).append(":").append(conn.getPort());
            }
            sb.append("</span><br/>Please check that the server is running on ")
                    .append("<br/>")
                    .append("Error detail is").append("<br/>")
                    .append(ex.getMessage())
                    .append("<br/>");
            showConsole(sb.toString());

        } catch (K4Exception ex) {
            StringBuilder sb = new StringBuilder();
            String hint = QErrors.lookup(ex.getMessage());
            sb.append("<span style=\"color:red\">")
                    .append("An error occurred during execution of the query.</span><br/> The server sent the response: ")
                    .append(ex.getMessage());
            if (hint != null) {
                sb.append("<br/><span style=\"color:green\">")
                        .append("Hint: Possibly this error refers to " + hint)
                        .append("</span>");
            }

            showConsole(sb.toString());
        } catch (java.lang.OutOfMemoryError ex) {
            StringBuilder sb = new StringBuilder("<span style=\"color:red\">Out of memory while communicating with server ");
            if (conn != null) {
                sb.append(conn.getHost()).append(":").append(conn.getPort());
            }
            sb.append("</span><br/>The result set is probably too large.<br/>Try increasing the memory available to Intellij IDEA");
            showConsole(sb.toString());
        } catch (Throwable ex) {

            String message = ex.getMessage();
            if ((message == null) || (message.length() == 0)) {
                message = "No message with exception. Exception is " + ex.toString();
            }

            StringBuilder sb = new StringBuilder("<span style=\"color:red\">An unexpected error occurred whilst communicating with server ");
            if (conn != null) {
                sb.append(conn.getHost()).append(":").append(conn.getPort());
            }
            sb.append("</span><br/>Error detail is<br/>").append(message);
            showConsole(sb.toString());
        }

    }

    public void blockRun() {
        this.blocked = true;
        executionStart = System.currentTimeMillis();
    }

    public static QGrid getInstance(Project project, boolean create) {
        QGrid instance = instanceMap.get(project);
        if (instance == null && create) {
            try {
                instance = new QGrid(project);
                EditorColorsManager.getInstance().addEditorColorsListener(instance);
                instanceMap.put(project, instance);
            } catch (Exception e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Plugin instantiation error", e.getMessage(), NotificationType.WARNING));
            }
        }
        return instance;
    }

    public static void closeInstance(Project project) {
        QGrid instance = instanceMap.get(project);
        if (instance != null) {
            //TODO: dispose instance
        }
        instanceMap.remove(project);
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
