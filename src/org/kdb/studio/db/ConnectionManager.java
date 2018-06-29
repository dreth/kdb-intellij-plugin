package org.kdb.studio.db;

import java.util.*;

public class ConnectionManager extends Observable {

    private static final ConnectionManager INSTANCE = new ConnectionManager();

    public static ConnectionManager getInstance() {
        return INSTANCE;
    }

    private Map<String, Connection> connections;

    private Connection activeConnection;

    public ConnectionManager() {
        connections = new LinkedHashMap();
    }

    public Connection[] getConnections() {
        return connections.values().toArray(new Connection[] {});
    }

    public void addOrUpdate(Connection connection) {
        Optional.ofNullable(connections.get(connection.getView())).ifPresent(Connection::close);
        connections.put(connection.getView(), connection);
        if (activeConnection != null && activeConnection.equals(connection)) {
            activeConnection = connection;
        }

    }

    public void remove(Connection connection) {
        connection.close();
        connections.remove(connection.getView());
        if (activeConnection != null && activeConnection.equals(connection)) {
            activeConnection = null;
        }
    }

    public void setActiveConnection(Connection activeConnection) {
        this.activeConnection = activeConnection;
    }

    public Connection getActiveConnection() {
        return activeConnection;
    }

    public Connection getConnectionByName(String name) {
        return connections.get(name);
    }

    public void releaseAll() {
        connections.values().forEach(Connection::close);
        connections.clear();
    }
}
