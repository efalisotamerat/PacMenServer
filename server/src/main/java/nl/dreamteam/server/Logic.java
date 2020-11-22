package nl.dreamteam.server;

import nl.dreamteam.server.models.Lobby;

import java.util.ArrayList;

public class Logic {
    private ArrayList<Lobby> lobbies = new ArrayList<>();

    public int createLobbyAndReturnId(String username){
        int lobbyId = lobbies.size() + 1;
        lobbies.add(new Lobby(lobbyId, username));
        return lobbyId;
    }

    public void joinLobby(int lobbyId, String username){
        lobbies.stream().filter(lobby -> lobby.getId() == lobbyId).findFirst().get().addPlayer(username);
    }
}
