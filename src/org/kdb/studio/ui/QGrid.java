package org.kdb.studio.ui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
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
import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class QGrid {

    static Color editorBgColor = UIManager.getColor("EditorPane.background");

    static Color editorFgColor = UIManager.getColor("EditorPane.foreground");

    static Font editorFont = UIManager.getFont("EditorPane.font");

    private JPanel panel1;
    private JTabbedPane tabbedPane1;

    private JTable table;
    private JTextPane textPane;
    private TableGroupPanel tableGroupPanel;

    private WidthAdjuster wa;

    private Project project;

    private AtomicBoolean errorLogged = new AtomicBoolean(false);

    private static Map<Project, QGrid> instanceMap = new WeakHashMap();

    private QGrid(Project project) {
        this.project = project;
        tableGroupPanel.disableAll();
        textPane.setBackground(editorBgColor);
        textPane.setForeground(editorFgColor);
        textPane.setFont(editorFont);
    }

    public void showTable(String query, KTableModel tableModel) {
        tabbedPane1.setEnabledAt(0, true);
        tabbedPane1.setEnabledAt(1, false);
        tabbedPane1.setSelectedIndex(0);
        tableGroupPanel.enableAll(query, tableModel, table);

        int rows = tableModel.getRowCount();
        this.table.setModel(tableModel);
        errorLogged.set(false);
        this.tabbedPane1.setTitleAt(0, "Table [" + rows + " rows]");
        wa.resizeAllColumns();
    }

    public JTable getTable() {
        return table;
    }

    public void showConsole(String sb) {
        tabbedPane1.setEnabledAt(0, false);
        tabbedPane1.setEnabledAt(1, true);
        tabbedPane1.setSelectedIndex(1);
        tableGroupPanel.disableAll();
        this.tabbedPane1.setTitleAt(0, "Table");
        textPane.setText("<html><body>" + sb + "</body></html>");
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
            } catch (IOException e) {

            }
            StringWriter writer = new StringWriter();
            try {
                lm.writeTo(writer);
            } catch (IOException e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Failed to show response in console", e.getMessage(),  NotificationType.WARNING));
            }
            showConsole(writer.toString());

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
            sb.append("A communications error occurred whilst sending the query ");
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
                    .append("An error occurred during execution of the query.</span><br/> The server sent the response:</br>")
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

    public static QGrid getInstance(Project project, boolean create) {
        QGrid instance = instanceMap.get(project);
        if (instance == null && create) {
            try {
                instance = new QGrid(project);
                instanceMap.put(project, instance);
            } catch (Exception e) {
                Notifications.Bus.notify(new Notification("KDBStudio", "Plugin instantiation error", e.getMessage(),  NotificationType.WARNING));
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
        table.setDefaultRenderer(KBase.class, new CellRenderer((title, content) -> {
            if (!errorLogged.get()) {
                Notifications.Bus.notify(new Notification("KDBStudio", title, content, NotificationType.WARNING));
                errorLogged.set(true);
            }
        }));
        table.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);
        wa = new WidthAdjuster(table);
        wa.resizeAllColumns();

    }

    @FunctionalInterface
    public interface ErrorLogger {
        void log(@NotNull String title, @NotNull String content);
    }
}
