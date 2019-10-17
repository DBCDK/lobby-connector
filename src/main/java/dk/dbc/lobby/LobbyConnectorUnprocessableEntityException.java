package dk.dbc.lobby;

public class LobbyConnectorUnprocessableEntityException extends LobbyConnectorUnexpectedStatusCodeException {

    public LobbyConnectorUnprocessableEntityException(String message) {
        super(message, 422);

    }

}
