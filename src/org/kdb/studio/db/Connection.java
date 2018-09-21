package org.kdb.studio.db;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.kdb.studio.kx.Connector;
import org.kdb.studio.kx.ConnectorFactory;

import java.util.Objects;

public class Connection {

    private String name;

    private String host;

    private int port;

    private String username;

    private char[] password;

    private boolean usePasswordVariable;

    private String passwordVariable;

    protected ObjectPool<Connector> connectorObjectPool;

    private final String SYNC = "SYNC";

    public Connection() {
    }

    public Connection(String name, String host, int port, String username, char[] password, boolean usePasswordVariable, String passwordVariable) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.usePasswordVariable = usePasswordVariable;
        this.passwordVariable = passwordVariable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public String getEffectivePassword() {
        if (usePasswordVariable && !StringUtil.isEmptyOrSpaces(passwordVariable)) {
            return System.getenv(passwordVariable);
        } else if (password != null){
            return new String(password);
        }
        return "";
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public boolean isUsePasswordVariable() {
        return usePasswordVariable;
    }

    public void setUsePasswordVariable(boolean usePasswordVariable) {
        this.usePasswordVariable = usePasswordVariable;
    }

    public String getPasswordVariable() {
        return passwordVariable;
    }

    public void setPasswordVariable(String passwordVariable) {
        this.passwordVariable = passwordVariable;
    }

    public String getView() {
        return name;
    }

    public ObjectPool<Connector> getConnectorPool() {
        synchronized (SYNC) {
            if (connectorObjectPool == null) {
                synchronized (SYNC) {
                    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
                    //config.setMinIdle(4);
                    //config.setTimeBetweenEvictionRunsMillis(500);
                    //config.setTestOnBorrow(true);
                    config.setTestOnReturn(true);
                    connectorObjectPool = new GenericObjectPool<>(new ConnectorFactory(this), config);
                }
            }
        }
        return connectorObjectPool;
    }

    public void close() {
        synchronized (SYNC) {
            if (connectorObjectPool != null) {
                connectorObjectPool.close();
                connectorObjectPool = null;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
