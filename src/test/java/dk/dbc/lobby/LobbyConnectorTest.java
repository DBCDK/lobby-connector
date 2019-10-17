/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.lobby;

import com.github.tomakehurst.wiremock.WireMockServer;
import dk.dbc.httpclient.HttpClient;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;

import java.time.Instant;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LobbyConnectorTest {

    private static WireMockServer wireMockServer;
    private static String wireMockHost;

    final static Client CLIENT = HttpClient.newClient(new ClientConfig()
            .register(new JacksonFeature()));
    static LobbyConnector connector;

    @BeforeAll
    static void startWireMockServer() {
        wireMockServer = new WireMockServer(options().dynamicPort()
                .dynamicHttpsPort());
        wireMockServer.start();
        wireMockHost = "http://localhost:" + wireMockServer.port();
        configureFor("localhost", wireMockServer.port());
    }

    @BeforeAll
    static void setConnector() {
        connector = new LobbyConnector(CLIENT, wireMockHost, LobbyConnector.TimingLogLevel.INFO);
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void testGetApplicants() throws LobbyConnectorException {
        LobbyConnector.Params params = new LobbyConnector.Params();
        params.withCategory("dpf");
        params.withState(LobbyConnector.Params.State.PENDING);

        Applicant[] actual = connector.getApplicants(params);

        assertThat(actual.length, is(1));
        assertThat(actual[0].getId(), is("1"));
        assertThat(actual[0].getCategory(), is("dpf"));
        assertThat(actual[0].getMimetype(), is("text/plain"));
        assertThat(actual[0].getState(), is(ApplicantState.PENDING));
        assertThat(actual[0].getBody(), nullValue());
        assertThat(actual[0].getTimeOfCreation(), is(Date.from(Instant.ofEpochMilli(1571212956165L)))); // Yes this is a clonky way to do it but Date(long) is deprecated
        assertThat(actual[0].getTimeOfLastModification(), is(Date.from(Instant.ofEpochMilli(1571212956165L))));
        assertThat(actual[0].getAdditionalInfo(), nullValue());
        assertThat(actual[0].getBodyLink(), is(wireMockHost + "/v1/api/applicants/1/body"));
    }

    @Test
    public void testConstructBodyLink() {
        Applicant applicant = new Applicant();
        applicant.setId("test-1");

        connector.constructBodyLink(applicant);

        assertThat(applicant.getBodyLink(), is(wireMockHost + "/v1/api/applicants/test-1/body"));
    }

}
