package server.businessrules;

import server.exceptions.MoveLimitException;
import server.game.Game;

// validator that checks whether the move limit of 200 has already been exceeded by the given game
public class MaxMoveValidator {
	
	public static void checkNoOfMoves(Game game, String currentPlayerId) throws MoveLimitException {
		if(game.getMoveCount() >= 200) {
			game.setLoser(currentPlayerId);
			throw new MoveLimitException("MaxMoveValidator", "This move would exceed the 200 moves limit");
		}
	}
}
