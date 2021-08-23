package org.kdb.studio;

import org.kdb.studio.chart.entity.Font;
import org.kdb.studio.db.AuthenticationDriverManager;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.ColorAndFontManager;

import java.util.*;

public class State {

    public Collection<Connection> connections;

    public Collection<AuthenticationDriver> authenticationDrivers;

    public String activeConnection;

    public Boolean toolbarEnabled;

    public Boolean formattingSupportEnabled;

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

        public String bgColor;

        public String authType;

        public Connection() {
        }

        public Connection(String name, String host, int port, String username, char[] password, boolean usePasswordVariable, String passwordVariable, boolean multilineCommentSupport, String bgColor, String authType) {
            this.name = name;
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = new String(password);
            this.usePasswordVariable = usePasswordVariable;
            this.passwordVariable = passwordVariable;
            this.multilineCommentSupport = multilineCommentSupport;
            this.bgColor = bgColor;
            this.authType = authType;
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
                    Objects.equals(multilineCommentSupport, that.multilineCommentSupport) &&
                    Objects.equals(bgColor, that.bgColor) &&
                    Objects.equals(authType, that.authType);
        }

        @Override
        public int hashCode() {

            int result = Objects.hash(name, host, port, username, password, usePasswordVariable, passwordVariable, multilineCommentSupport, bgColor);
            return result;
        }
    }

    public static class AuthenticationDriver {
        public String name;
        public String className;
        public Collection<String> jars;

        public AuthenticationDriver() {
        }

        public AuthenticationDriver(String name, String className, Collection<String> jars) {
            this.name = name;
            this.className = className;
            this.jars = jars;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuthenticationDriver that = (AuthenticationDriver) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(className, that.className) &&
                    Objects.equals(jars, that.jars);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, className, jars);
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

    public Collection<AuthenticationDriver> getAuthenticationDrivers() {
        return authenticationDrivers;
    }

    public void setAuthenticationDrivers(Collection<AuthenticationDriver> authenticationDrivers) {
        this.authenticationDrivers = authenticationDrivers;
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

    public Boolean getFormattingSupportEnabled() {
        return formattingSupportEnabled;
    }

    public void setFormattingSupportEnabled(Boolean formattingSupportEnabled) {
        this.formattingSupportEnabled = formattingSupportEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(connections, state.connections) &&
                Objects.equals(authenticationDrivers, state.authenticationDrivers) &&
                Objects.equals(activeConnection, state.activeConnection) &&
                Objects.equals(toolbarEnabled, state.toolbarEnabled) &&
                Objects.equals(formattingSupportEnabled, state.formattingSupportEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connections, authenticationDrivers, activeConnection, toolbarEnabled, formattingSupportEnabled);
    }

    static State create(ConnectionManager connectionManager, AuthenticationDriverManager authenticationDriverManager, ColorAndFontManager colorAndFontManager, boolean toolbarEnabled) {
        State state = new State();
        state.setConnections(new LinkedList<>());
        state.setAuthenticationDrivers(new LinkedList<>());
        Optional.ofNullable(connectionManager.getActiveConnection()).ifPresent(conn -> state.setActiveConnection(conn.getView()));
        for (org.kdb.studio.db.Connection conn : connectionManager.getConnections()) {
            state.getConnections().add(new Connection(conn.getName(), conn.getHost(), conn.getPort(), conn.getUsername(), conn.getPassword(), conn.isUsePasswordVariable(), conn.getPasswordVariable(), conn.isMultilineCommentSupport(), conn.getBgColor(), conn.getAuthType()));
        }
        for (org.kdb.studio.db.AuthenticationDriver driver: authenticationDriverManager.getAuthenticationDrivers()) {
            state.getAuthenticationDrivers().add(new AuthenticationDriver(driver.getName(), driver.getClazz(), driver.getJarFiles()));
        }
        state.setToolbarEnabled(toolbarEnabled);
        state.styles.clear();
        state.setFormattingSupportEnabled(colorAndFontManager.getFormattingEnabled());
        colorAndFontManager.getFontMap().forEach((key, font) -> state.styles.put(key, Font.fromAwtFont(font)));
        return state;
    }

    void apply(ConnectionManager connectionManager, AuthenticationDriverManager authenticationDriverManager) {
        connectionManager.releaseAll();
        connections.forEach(connection -> connectionManager.addOrUpdate(new org.kdb.studio.db.Connection(connection.name, connection.host, connection.port, connection.username, connection.password.toCharArray(), connection.usePasswordVariable, connection.passwordVariable, connection.multilineCommentSupport, connection.bgColor, connection.authType)));
        authenticationDrivers.forEach(driver -> authenticationDriverManager.addOrUpdate(new org.kdb.studio.db.AuthenticationDriver(driver.name, driver.className, new HashSet<>(driver.jars))));
        connectionManager.setActiveConnection(connectionManager.getConnectionByName(activeConnection));
    }

    void apply(ColorAndFontManager colorAndFontManager) {
        colorAndFontManager.getFontMap().clear();
        styles.forEach((key, font) -> colorAndFontManager.getFontMap().put(key, Font.toAwtFont(font)));
        colorAndFontManager.setFormattingEnabled(Optional.ofNullable(formattingSupportEnabled).orElse(true));
    }
}
