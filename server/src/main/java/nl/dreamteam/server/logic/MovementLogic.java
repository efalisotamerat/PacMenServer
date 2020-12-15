package nl.dreamteam.server.logic;

import nl.dreamteam.server.Enums.Direction;
import nl.dreamteam.server.Enums.PlayerType;
import nl.dreamteam.server.abstracts.GameObject;
import nl.dreamteam.server.controllers.MessageController;
import nl.dreamteam.server.models.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;

public class MovementLogic {

    private final int movementDistance = 4;

    private void move(Player player, Position position){
        player.setPosition(position);
    }

    private Position getNextPosition(Position currentPos, Direction direction){
        switch (direction){
            case Right:
                return new Position(currentPos.getX() + movementDistance, currentPos.getY());
            case Left:
                return new Position(currentPos.getX() - movementDistance, currentPos.getY());
            case Up:
                return new Position(currentPos.getX(), currentPos.getY() - movementDistance);
            case Down:
                return new Position(currentPos.getX(), currentPos.getY() + movementDistance);
            default: return currentPos;
        }
    }

    public void tryMove(String username, Direction direction, Lobby lobby, MessageController messageController){
        Player lobbyPlayer = lobby.getPlayer(username);
        lobbyPlayer.setCurrentDirection(direction);

        Position nextPos = getNextPosition(lobbyPlayer.getPosition(), direction);
        Position convertedPos = convertPositionToMapPosition(nextPos, direction);
        if(isPathBlocked(convertedPos, lobby.getMap().getGameObjects())){
            return;
        }
        if(collidesWithOpponent(lobbyPlayer, convertedPos, lobby.getPlayers())){
            lobby.resetPlayers();
            messageController.UpdatePlayerMovement(lobby.getPlayers());
            return;
        }
        Dot dot = collidesWithDot(convertedPos, lobby.getMap().getGameObjects());
        if(dot != null && lobbyPlayer.getPlayerType() == PlayerType.PACMAN) {
            lobbyPlayer.addScore(dot.getValue());
            GameObject[][] objects = lobby.getMap().getGameObjects();
            objects[dot.getPosition().getX()][dot.getPosition().getY()] = null;
            lobby.getMap().setGameObjects(objects);
            messageController.UpdatePacmanDots(lobby.getPlayers(), dot);
        }
        move(lobbyPlayer, nextPos);

        messageController.UpdatePlayerMovement(lobby.getPlayers());
    }

    private Position convertPositionToMapPosition(Position oldPosition, Direction direction){
        int newX;
        int newY;
        if(isHorizontalMovement(direction)){
            newX = (int)Math.ceil(oldPosition.getX() / (double)Lobby.squareWidth);
            newY = oldPosition.getY() / Lobby.squareWidth;
        } else {
            newX = oldPosition.getX() / Lobby.squareWidth;
            newY = (int)Math.ceil(oldPosition.getY() / (double)Lobby.squareWidth);
        }
        return new Position(newX, newY);
    }

    private boolean isHorizontalMovement(Direction direction){
        return direction == Direction.Left || direction == Direction.Right;
    }

    private boolean isPathBlocked(Position nextPosition, GameObject[][] objects){
        if(objects[nextPosition.getY()][nextPosition.getX()] instanceof Wall){
            return true;
        }
        return false;
    }

    private Dot collidesWithDot(Position nextPosition, GameObject[][] objects){
        if(objects[nextPosition.getX()][nextPosition.getY()] instanceof Dot){
            return (Dot) objects[nextPosition.getX()][nextPosition.getY()];
        }
        return null;
    }

    private boolean collidesWithOpponent(Player currentPlayer, Position nextPos, ArrayList<Player> players){
        for(Player player: players) {
            if(player.getPosition().getX() == nextPos.getX() && player.getPosition().getY() == nextPos.getY() && isOpponent(currentPlayer.getPlayerType(), player.getPlayerType())) {
                return true;
            }
        }
        return false;
    }

    private boolean isOpponent(PlayerType playerType, PlayerType opponentType) {
        int ghostCounter = 0;
        String player = playerType.toString();
        if(isGhost(player)) {
            ghostCounter++;
        }
        String opponent = opponentType.toString();
        if(isGhost(opponent)) {
            ghostCounter++;
        }

        if(ghostCounter == 1) {
            return true;
        }
        return false;
    }

    private boolean isGhost(String input) {
        return input.indexOf("GHOST") !=-1? true: false;
    }
}
