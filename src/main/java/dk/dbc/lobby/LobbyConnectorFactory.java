/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.lobby;

import dk.dbc.httpclient.HttpClient;
import dk.dbc.lobby.LobbyConnector.TimingLogLevel;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;

/**
 * LobbyConnector factory
 * <p>
 * Synopsis:
 * </p>
 * <pre>
 *    // New instance
 *    LobbyConnector lc = LobbyConnectorFactory.create("http://record-service");
 *
 *    // Singleton instance in CDI enabled environment
 *    {@literal @}Inject
 *    LobbyConnectorFactory factory;
 *    ...
 *    LobbyConnector lc = factory.getInstance();
 *
 *    // or simply
 *    {@literal @}Inject
 *    LobbyConnector lc;
 * </pre>
 * <p>
 * CDI case depends on the lobby service baseurl being defined as
 * the value of either a system property or environment variable
 * named LOBBY_SERVICE_URL. LOBBY_SERVICE_TIMING_LOG_LEVEL
 * should be one of TRACE, DEBUG, INFO(default), WARN or ERROR, for setting
 * log level
 * </p>
 */
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
    private TimingLogLevel level;

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
