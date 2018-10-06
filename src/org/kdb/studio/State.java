package org.kdb.studio;

import org.kdb.studio.chart.entity.Font;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.ColorAndFontManager;

import java.util.*;

public class State {

    public Collection<Connection> connections;

    public String activeConnection;

    public Boolean toolbarEnabled;

    public Map<String, Font> styles = new LinkedHashMap<>();

    public static class Connection {

        public String name;

        public String host;

        public int port;

        public String username;

        public String password;

        public boolean usePasswordVariable;

        public String passwordVariable;

        public boolean multilineCommentSupport;

        public Connection() {
        }

        public Connection(String name, String host, int port, String username, char[] password, boolean usePasswordVariable, String passwordVariable, boolean multilineCommentSupport) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = new String(password);
            this.usePasswordVariable = usePasswordVariable;
            this.passwordVariable = passwordVariable;
            this.multilineCommentSupport = multilineCommentSupport;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Connection that = (Connection) o;
            return port == that.port &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(host, that.host) &&
                    Objects.equals(username, that.username) &&
                    Objects.equals(password, that.password) &&
                    Objects.equals(usePasswordVariable, that.usePasswordVariable) &&
                    Objects.equals(passwordVariable, that.passwordVariable) &&
                    Objects.equals(multilineCommentSupport, this.multilineCommentSupport);
        }

        @Override
        public int hashCode() {

            int result = Objects.hash(name, host, port, username, password, usePasswordVariable, passwordVariable, multilineCommentSupport);
            return result;
        }
    }

    public State() {
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    public void setConnections(Collection<Connection> connections) {
        this.connections = connections;
    }

    public String getActiveConnection() {
        return activeConnection;
    }

    public void setActiveConnection(String activeConnection) {
        this.activeConnection = activeConnection;
    }

    public Boolean getToolbarEnabled() {
        return toolbarEnabled;
    }

    public void setToolbarEnabled(Boolean toolbarEnabled) {
        this.toolbarEnabled = toolbarEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(connections, state.connections) &&
                Objects.equals(activeConnection, state.activeConnection) &&
                Objects.equals(toolbarEnabled, state.toolbarEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connections, activeConnection, toolbarEnabled);
    }

    static State create(ConnectionManager connectionManager, ColorAndFontManager colorAndFontManager, boolean toolbarEnabled) {
        State state = new State();
        state.setConnections(new LinkedList<>());
        Optional.ofNullable(connectionManager.getActiveConnection()).ifPresent(conn -> state.setActiveConnection(conn.getView()));
        for (org.kdb.studio.db.Connection conn : connectionManager.getConnections()) {
            state.getConnections().add(new Connection(conn.getName(), conn.getHost(), conn.getPort(), conn.getUsername(), conn.getPassword(), conn.isUsePasswordVariable(), conn.getPasswordVariable(), conn.isMultilineCommentSupport()));
        }
        state.setToolbarEnabled(toolbarEnabled);
        state.styles.clear();
        colorAndFontManager.getFontMap().forEach((key, font) -> state.styles.put(key, Font.fromAwtFont(font)));
        return state;
    }

    void apply(ConnectionManager connectionManager) {
        connectionManager.releaseAll();
        connections.forEach(connection -> connectionManager.addOrUpdate(new org.kdb.studio.db.Connection(connection.name, connection.host, connection.port, connection.username, connection.password.toCharArray(), connection.usePasswordVariable, connection.passwordVariable, connection.multilineCommentSupport)));
        connectionManager.setActiveConnection(connectionManager.getConnectionByName(activeConnection));
    }

    void apply(ColorAndFontManager colorAndFontManager) {
        colorAndFontManager.getFontMap().clear();
        styles.forEach((key, font) -> colorAndFontManager.getFontMap().put(key, Font.toAwtFont(font)));
    }
}
