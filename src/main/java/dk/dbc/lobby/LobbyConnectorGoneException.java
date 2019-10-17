package dk.dbc.lobby;

public class LobbyConnectorGoneException extends LobbyConnectorUnexpectedStatusCodeException {

    public LobbyConnectorGoneException(String message) {
        super(message, 410);
    }

}
