package server.main;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import MessagesBase.HalfMap;
import MessagesBase.PlayerRegistration;
import MessagesBase.ResponseEnvelope;
import MessagesBase.UniqueGameIdentifier;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.GameState;
import server.businessrules.IDValidator;
import server.businessrules.MaxMoveValidator;
import server.businessrules.TimeValidator;
import server.businessrules.TurnValidator;
import server.exceptions.GenericExampleException;
import server.game.Game;
import server.game.GameIdGenerator;
import server.halfmapvalidator.ValidationManager;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {
	
	// HashMap that contains all the currently active games
	// and List of all game IDs ordered by time of creation
	private HashMap<String, Game> games = new HashMap<>();
	private List<String> gamesListSortedByCreationTime = new ArrayList<>(); 

	// Client requests a game ID for a new game
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {

		// check if server still has capacity to allow new games 
		if(games.size() >= 999) {
			// remove oldest game if capacity reached
			games.remove(gamesListSortedByCreationTime.get(0));
			gamesListSortedByCreationTime.remove(0);
		}
		
		// add the new game to list of games
		String newGameId = GameIdGenerator.generate();
		Game newGame = new Game(newGameId);
		games.put(newGameId, newGame);
		gamesListSortedByCreationTime.add(newGameId);
		
		UniqueGameIdentifier gameIdentifier = new UniqueGameIdentifier(newGameId);
		return gameIdentifier;
	}

	// Client wants to register itself for a game
	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {
		UniquePlayerIdentifier newPlayerID = new UniquePlayerIdentifier(UUID.randomUUID().toString());
		
		// register the player for the game after registration has passed basic id check
		IDValidator.checkGameId(gameID.getUniqueGameID(), games.values());
		ResponseEnvelope<UniquePlayerIdentifier> playerIDMessage = new ResponseEnvelope<>(newPlayerID);
		String firstName = playerRegistration.getStudentFirstName();
		String lastName = playerRegistration.getStudentLastName();
		String studentid = playerRegistration.getStudentID();
		games.get(gameID.getUniqueGameID()).addPlayer(firstName, lastName, studentid, EPlayerGameState.MustWait, newPlayerID.getUniquePlayerID());
		
		return playerIDMessage;
	}
	
	// Client sends its halfmap
	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<String> receiveHalfMap(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody HalfMap halfMap) {
		
		// check whether this game and player exist at all
		IDValidator.checkGameId(gameID.getUniqueGameID(), games.values());
		IDValidator.checkPlayerId(halfMap.getUniquePlayerID(), games.get(gameID.getUniqueGameID()));
		try {
			// check whether halfmap fulfills all business rules
			TurnValidator.thisPlayersTurn(halfMap.getUniquePlayerID(), games.get(gameID.getUniqueGameID()));
			TimeValidator.checkThreeSecondsRule(games.get(gameID.getUniqueGameID()).getLastActionTime());
			MaxMoveValidator.checkNoOfMoves(games.get(gameID.getUniqueGameID()),halfMap.getUniquePlayerID());
			ValidationManager.checkMapRules(halfMap);
		}
		catch(GenericExampleException invalidHalfMap) {
			// if one rule is broken: rulebreaker is determined to be loser, then forward error to error handling method
			games.get(gameID.getUniqueGameID()).setLoser(halfMap.getUniquePlayerID());
			throw invalidHalfMap;
		}
		
		// if checks were passed: add half map to game
		ValidationManager.checkMapRules(halfMap);
		games.get(gameID.getUniqueGameID()).addHalfMap(halfMap);
		games.get(gameID.getUniqueGameID()).increaseMoveCount();
		ResponseEnvelope<String> answer = new ResponseEnvelope<String>("HalfMap sent successfully"); // CHANGE?
		
		return answer;
	}
	
	// Client requests to get current GameState
	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<GameState> sendGameState(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition,
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID) {

		// check whether game and player exist
		IDValidator.checkGameId(gameID.getUniqueGameID(), games.values());
		IDValidator.checkPlayerId(playerID.getUniquePlayerID(), games.get(gameID.getUniqueGameID()));
		
		// create specific gamestate and send to client
		GameState answerState = games.get(gameID.getUniqueGameID()).createPlayerSpecificCurrentGameState(playerID);
		ResponseEnvelope<GameState> answer = new ResponseEnvelope<GameState>(answerState); // WHY ENVELOPE HERE?

		return answer;
	}
	
	// @Scheduled(fixedRate = 10000) -> Time validation outsourced into TimeValidator
	

	/*
	 * of try/catch applies also here. Hence, we recommend to simply extend your own
	 * Exceptions from the GenericExampleException. For larger projects one would
	 */
	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		// reply with 200 OK as defined in the network documentation
		// Side note: We only do this here for simplicity reasons. For future projects,
		// you should check out HTTP status codes and
		// what they can be used for. Note, the WebClient used on the client can react
		// to them using the .onStatus(...) method.
		Logger.getAnonymousLogger().log(Level.SEVERE, "A client-caused error occured, 'name : message' " + ex.getErrorName() + " : " + ex.getMessage());
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
	

}
