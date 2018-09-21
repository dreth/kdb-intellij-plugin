package org.kdb.studio.kx;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.kdb.studio.db.Connection;

public class ConnectorFactory extends BasePooledObjectFactory<Connector> {

    private Connection connection;

    public ConnectorFactory(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connector create() throws Exception {
        String password = connection.getEffectivePassword();
        String up = StringUtil.isEmptyOrSpaces(connection.getUsername()) ? "" : connection.getUsername() + (StringUtil.isEmptyOrSpaces(password) ? "" : ":" + password);
        Connector connector = new Connector(connection.getHost(), connection.getPort(), up);
        return connector;
    }

    @Override
    public PooledObject<Connector> wrap(Connector connector) {
        return new DefaultPooledObject<>(connector);
    }

    @Override
    public void destroyObject(PooledObject<Connector> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<Connector> p) {
        return !p.getObject().isClosed();
    }
}
