package nl.dreamteam.server.logic;

import nl.dreamteam.server.models.Player;
import nl.dreamteam.server.models.Point;

public class MovementLogic {

    private LobbyLogic lobbyLogic;

    public MovementLogic(LobbyLogic lobbyLogic){
        this.lobbyLogic = lobbyLogic;
    }

    public void move(Player player, Point position){
        player.setPosition(position);
    }
}