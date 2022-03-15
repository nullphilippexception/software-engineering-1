package server.businessrules;

import server.exceptions.WrongTurnException;
import server.game.Game;

// validator that checks whether the given game actually expects the given player to act
public class TurnValidator {
	public static void thisPlayersTurn(String playerId, Game game) throws WrongTurnException {
		if(!game.getCurrentTurnId().equals(playerId)) {
			throw new WrongTurnException("TurnValidator", "Player acted even though it was not its turn");
		}
	}
}
