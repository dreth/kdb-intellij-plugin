package org.kdb.studio.kx;

import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.kdb.studio.db.AuthenticationDriver;
import org.kdb.studio.db.AuthenticationDriverManager;
import org.kdb.studio.db.Connection;

import java.util.function.Function;

public class ConnectorFactory extends BasePooledObjectFactory<Connector> {

    private Connection connection;

    private AuthenticationDriverManager authenticationDriverManager;

    public ConnectorFactory(Connection connection) {
        this.connection = connection;
        this.authenticationDriverManager = AuthenticationDriverManager.getInstance();
    }

    @Override
    public Connector create() throws Exception {
        String password = connection.getEffectivePassword();
        authenticationDriverManager.validateAuthenticationDriverByName(connection.getAuthType());
        Function<String, String> authenticator = authenticationDriverManager.getAuthenticationDriverByName(connection.getAuthType())
                .map(AuthenticationDriver::newAuthenticator).orElseGet(AuthenticationDriver::getBasicAuthenticator);
        String up = authenticator.apply(String.format("%s:%s@%s:%d", connection.getUsername() , password, connection.getHost(), connection.getPort()));
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
