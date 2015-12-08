package com.mdtech.social.connect;

import com.mdtech.social.api.PanoramioOperations;
import com.mdtech.social.api.Ponmap;
import com.mdtech.social.api.PonmapProfile;
import com.mdtech.social.api.UserOperations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.web.client.HttpClientErrorException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PonmapConnectionFactoryTest {

    private Log log = LogFactory.getLog(PonmapConnectionFactoryTest.class);

    private ConnectionFactoryRegistry connectionFactoryRegistry;
    private OAuth2ConnectionFactory connectionFactory;
    private ConnectionRepository connectionRepository;

    @Before
    public void before() {
        connectionFactoryRegistry = new ConnectionFactoryRegistry();
        connectionFactoryRegistry.addConnectionFactory(new PonmapConnectionFactory("ponmap-android-client", "92jasdjf8923oda"));
        connectionFactory = (OAuth2ConnectionFactory)connectionFactoryRegistry.getConnectionFactory("ponmap");

        connectionRepository = new InMemoryUsersConnectionRepository(connectionFactoryRegistry).createConnectionRepository("user");
    }

    /**
     * 测试客户端授权
     */
    @Test
    public void testAuthenticateClient() {
        // 客户端授权
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().authenticateClient("read");

        assertNotNull(accessGrant);

        assertNotNull(accessGrant.getAccessToken());

        Connection<Ponmap> connection = connectionFactory.createConnection(accessGrant);
        connectionRepository.addConnection(connection);
        connection = connectionRepository.findPrimaryConnection(Ponmap.class);
        assertTrue(!connection.hasExpired());
        Ponmap ponmapApi = connection.getApi();
        assertNotNull(ponmapApi);
        PanoramioOperations panoramioOperations = ponmapApi.panoramioOperations();
        assertNotNull(panoramioOperations);

    }

    /**
     * 测试用户授权
     */
    @Test
    public void testUserLogin() {
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeCredentialsForAccess("user", "user", null);

        assertNotNull(accessGrant);

        assertNotNull(accessGrant.getAccessToken());

        Connection<Ponmap> connection = connectionFactory.createConnection(accessGrant);
        // 删除所有已存在连接
        connectionRepository.removeConnections(PonmapServiceProvider.PROVIDER_ID);
        connectionRepository.addConnection(connection);
        connection = connectionRepository.findPrimaryConnection(Ponmap.class);
        assertTrue(!connection.hasExpired());
        Ponmap ponmapApi = connection.getApi();
        assertNotNull(ponmapApi);

        UserOperations userOperations = ponmapApi.userOperations();
        assertNotNull(userOperations);

        PonmapProfile profile = userOperations.getUserProfile();

        assertThat(profile.getUsername(), is("user"));
    }

    @Test
    public void sample() {

        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeCredentialsForAccess("user", "user", null);

        assertNotNull(accessGrant);

        assertNotNull(accessGrant.getAccessToken());

        Connection<Ponmap> connection = connectionFactory.createConnection(accessGrant);

        assertNotNull(connection);

        Ponmap ponmapApi = connection.getApi();

        assertNotNull(ponmapApi);

        UserOperations userOperations = ponmapApi.userOperations();

        assertNotNull(userOperations);

        PonmapProfile profile = userOperations.getUserProfile("26449692454748190120520877226");

        assertNotNull(profile);
        assertNotNull(profile.getId());
        assertEquals("26449692454748190120520877226", profile.getId());
        assertEquals("user", profile.getUsername());
        assertEquals("暗梅幽闻花", profile.getName());

    }

    @Test
    public void testErrorLogin() {
        // 错误用户名密码登录失败测试
        String username = "user";
        String password = "error";
        try {
            AccessGrant accessGrant = connectionFactory.getOAuthOperations()
                    .exchangeCredentialsForAccess(username, password, null);

        }catch (HttpClientErrorException ex) {
            assertEquals(400, ex.getStatusCode().value());
        }
    }
}
