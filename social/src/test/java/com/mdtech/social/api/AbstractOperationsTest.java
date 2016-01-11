package com.mdtech.social.api;

import com.mdtech.social.connect.HereConnectionFactory;
import com.mdtech.social.connect.HereServiceProvider;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;

import static org.junit.Assert.*;

/**
 * Created by any on 2014/10/31.
 */
public abstract class AbstractOperationsTest {

    ConnectionFactoryRegistry connectionFactoryRegistry;
    OAuth2ConnectionFactory connectionFactory;
    ConnectionRepository connectionRepository;

    public void setup() {
        connectionFactoryRegistry = new ConnectionFactoryRegistry();
        connectionFactoryRegistry.addConnectionFactory(new HereConnectionFactory("ponmap-android-client", "92jasdjf8923oda"));
        connectionFactory = (OAuth2ConnectionFactory)connectionFactoryRegistry.getConnectionFactory(HereServiceProvider.PROVIDER_ID);
        connectionRepository = new InMemoryUsersConnectionRepository(connectionFactoryRegistry).createConnectionRepository("user");
    }

    public void authenticateClient() {
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().authenticateClient("read");

        assertNotNull(accessGrant);
        assertNotNull(accessGrant.getAccessToken());

        Connection<HereApi> connection = connectionFactory.createConnection(accessGrant);
        connectionRepository.addConnection(connection);
    }

    protected void login() {
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeCredentialsForAccess("user", "user", null);

        assertNotNull(accessGrant);
        assertNotNull(accessGrant.getAccessToken());

        Connection<HereApi> connection = connectionFactory.createConnection(accessGrant);
        // 删除所有已存在连接
        connectionRepository.removeConnections(HereServiceProvider.PROVIDER_ID);
        connectionRepository.addConnection(connection);
        connection = connectionRepository.findPrimaryConnection(HereApi.class);
        assertTrue(!connection.hasExpired());

    }

}
