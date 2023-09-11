package dk.dbc.lobby;

public class LobbyConnectorTestWireMockRecorder {
        /*
        Steps to reproduce wiremock recording:

        * Start standalone runner
            java -jar wiremock-standalone-{WIRE_MOCK_VERSION}.jar --proxy-all="{RECORD_SERVICE_HOST}" --record-mappings --verbose

        * Run the main method of this class

        * Replace content of src/test/resources/{__files|mappings} with that produced by the standalone runner
     */

    public static void main(String[] args) throws Exception {
        LobbyConnectorTest.connector = new LobbyConnector(
                LobbyConnectorTest.CLIENT, "http://localhost:8080");
        final LobbyConnectorTest lobbyConnectorTest = new LobbyConnectorTest();
        recordGetApplicantRequests(lobbyConnectorTest);
    }

    private static void recordGetApplicantRequests(LobbyConnectorTest lobbyConnectorTest)
            throws LobbyConnectorException {
        lobbyConnectorTest.testGetApplicants();
    }

}
