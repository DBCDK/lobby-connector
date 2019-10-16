/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.lobby;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.httpclient.HttpGet;
import dk.dbc.httpclient.PathBuilder;
import dk.dbc.invariant.InvariantUtil;
import dk.dbc.util.Stopwatch;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class LobbyConnector {

    public enum TimingLogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyConnector.class);
    private static final String PATH_VARIABLE_APPLICANT_ID = "id";
    private static final String PATH_GET_APPLICANTS = "/v1/api/applicants";
    private static final String PATH_GET_APPLICANT = String.format("/v1/api/applicants/{%s}",
            PATH_VARIABLE_APPLICANT_ID);

    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(Collections.singletonList(ProcessingException.class))
            .retryIf((Response response) -> response.getStatus() == 404
                    || response.getStatus() == 500
                    || response.getStatus() == 502)
            .withDelay(10, TimeUnit.SECONDS)
            .withMaxRetries(6);

    private final FailSafeHttpClient failSafeHttpClient;
    private final String baseUrl;
    private final LobbyConnector.LogLevelMethod logger;

    /**
     * Returns new instance with default retry policy
     *
     * @param httpClient web resources client
     * @param baseUrl    base URL for lobby service endpoint
     */
    public LobbyConnector(Client httpClient, String baseUrl) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl, LobbyConnector.TimingLogLevel.INFO);
    }

    /**
     * Returns new instance with default retry policy
     *
     * @param httpClient web resources client
     * @param baseUrl    base URL for lobby service endpoint
     * @param level      timings log level
     */
    public LobbyConnector(Client httpClient, String baseUrl, LobbyConnector.TimingLogLevel level) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl, level);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for lobby service endpoint
     */
    public LobbyConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl) {
        this(failSafeHttpClient, baseUrl, LobbyConnector.TimingLogLevel.INFO);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for lobby service endpoint
     * @param level              timings log level
     */
    public LobbyConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl, LobbyConnector.TimingLogLevel level) {
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(
                failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(
                baseUrl, "baseUrl");
        switch (level) {
            case TRACE:
                logger = LOGGER::trace;
                break;
            case DEBUG:
                logger = LOGGER::debug;
                break;
            case INFO:
                logger = LOGGER::info;
                break;
            case WARN:
                logger = LOGGER::warn;
                break;
            case ERROR:
                logger = LOGGER::error;
                break;
            default:
                logger = LOGGER::info;
                break;
        }
    }

    public Applicant[] getApplicants(Params params) throws LobbyConnectorException {
        final Stopwatch stopwatch = new Stopwatch();
        try {
            return sendRequest(PATH_GET_APPLICANTS, params, Applicant[].class);
        } finally {
            logger.log("getApplicants() took {} milliseconds",
                    stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));
        }
    }

    private <T> T sendRequest(String basePath, Params params, Class<T> type)
            throws LobbyConnectorException {
        final PathBuilder path = new PathBuilder(basePath);
        final HttpGet httpGet = new HttpGet(failSafeHttpClient)
                .withBaseUrl(baseUrl)
                .withPathElements(path.build());
        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                httpGet.withQueryParameter(param.getKey(), param.getValue());
            }
        }
        final Response response = httpGet.execute();
        assertResponseStatus(response, Response.Status.OK);
        return readResponseEntity(response, type);
    }

    private <T> T readResponseEntity(Response response, Class<T> type)
            throws LobbyConnectorException {
        final T entity = response.readEntity(type);
        if (entity == null) {
            throw new LobbyConnectorException(
                    String.format("Lobby service returned with null-valued %s entity",
                            type.getName()));
        }
        return entity;
    }

    private void assertResponseStatus(Response response, Response.Status expectedStatus)
            throws LobbyConnectorUnexpectedStatusCodeException {
        final Response.Status actualStatus =
                Response.Status.fromStatusCode(response.getStatus());
        if (actualStatus != expectedStatus) {
            throw new LobbyConnectorUnexpectedStatusCodeException(
                    String.format("Lobby service returned with unexpected status code: %s",
                            actualStatus),
                    actualStatus.getStatusCode());
        }
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    @FunctionalInterface
    interface LogLevelMethod {
        void log(String format, Object... objs);
    }

    public static class Params extends HashMap<String, Object> {
        public enum Key {
            STATE("state"),
            CATEGORY("category");

            private final String keyName;

            Key(String keyName) {
                this.keyName = keyName;
            }

            public String getKeyName() {
                return keyName;
            }
        }

        public enum State {
            ACCEPTED, PENDING;

            @Override
            public String toString() {
                return name();
            }
        }

        public Params withState(State state) {
            putOrRemoveOnNull(Key.STATE, state);
            return this;
        }

        public Optional<State> getState() {
            return Optional.ofNullable((State) this.get(Key.STATE));
        }

        public Params withCategory(String category) {
            putOrRemoveOnNull(Key.CATEGORY, category);
            return this;
        }

        public Optional<String> getCategory() {
            return Optional.ofNullable((String) this.get(Key.CATEGORY));
        }

        private void putOrRemoveOnNull(Key param, Object value) {
            if (value == null) {
                this.remove(param.keyName);
            } else {
                this.put(param.keyName, value);
            }
        }
    }

}
