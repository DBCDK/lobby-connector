package dk.dbc.lobby;

import dk.dbc.httpclient.HttpClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;

@ApplicationScoped
public class LobbyConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyConnectorFactory.class);

    public static LobbyConnector create(String lobbyServiceBaseUrl) {
        final Client client = HttpClient.newClient(new ClientConfig()
                .register(new JacksonFeature()));
        LOGGER.info("Creating LobbyConnector for: {}", lobbyServiceBaseUrl);
        return new LobbyConnector(client, lobbyServiceBaseUrl);
    }

    public static LobbyConnector create(String lobbyServiceBaseUrl, LobbyConnector.TimingLogLevel level) {
        final Client client = HttpClient.newClient(new ClientConfig()
                .register(new JacksonFeature()));
        LOGGER.info("Creating LobbyConnector for: {}", lobbyServiceBaseUrl);
        return new LobbyConnector(client, lobbyServiceBaseUrl, level);
    }

    @Inject
    @ConfigProperty(name = "LOBBY_SERVICE_URL")
    private String lobbyServiceUrl;

    @Inject
    @ConfigProperty(name = "LOBBY_LOG_LEVEL", defaultValue = "INFO")
    private LobbyConnector.TimingLogLevel level;

    LobbyConnector lobbyConnector;

    @PostConstruct
    public void initializeConnector() {
        lobbyConnector = LobbyConnectorFactory.create(lobbyServiceUrl, level);
    }

    @Produces
    public LobbyConnector getInstance() {
        return lobbyConnector;
    }

    @PreDestroy
    public void tearDownConnector() {
        lobbyConnector.close();
    }
}
