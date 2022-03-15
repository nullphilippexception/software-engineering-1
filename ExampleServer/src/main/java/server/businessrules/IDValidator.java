package server.businessrules;
import java.util.Collection;
import java.util.List;

import server.exceptions.InvalidIdentifierException;
import server.game.*;

// validator that checks whether the given id (player or game) actually exists
public class IDValidator {
	
	// check if game id exists
	public static void checkGameId(String id, Collection<Game> games) throws InvalidIdentifierException{
		for(Game game : games) {
			if(game.getGameId().equals(id)) return; // id successfully found
		}
		throw new InvalidIdentifierException("IDValidator", "This game does not exist");
	}
	
	// check if player id exists for given game
	public static void checkPlayerId(String id, Game game) {
		List<String> ids = game.getPlayerIds();
		for(String current_id : ids) {
			if(current_id.equals(id)) return; // id successfully found
		}
		throw new InvalidIdentifierException("IDValidator", "This player does not exist");
	}
}
