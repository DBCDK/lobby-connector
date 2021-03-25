# Updateservice Rest Connector
Jar library containing helper functions for calling the lobby service

### Usage
In pom.xml add this dependency:

    <groupId>dk.dbc</groupId>
    <artifactId>lobby-connector</artifactId>
    <version>1.1-SNAPSHOT</version>

In your EJB add the following inject:

    @Inject
    private LobbyConnector lobbyConnector;

You must have the following environment variables in your deployment:

    LOBBY_SERVICE_URL

### Examples
        LobbyConnector.Params params = new LobbyConnector.Params();
        params.withCategory("dpf");
        params.withState(LobbyConnector.Params.State.PENDING);

        Applicant[] applicants = lobbyConnector.getApplicants(params);
