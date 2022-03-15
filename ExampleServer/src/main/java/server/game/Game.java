package server.game;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import MessagesBase.HalfMap;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.EPlayerGameState;
import MessagesGameState.FullMap;
import MessagesGameState.GameState;
import MessagesGameState.PlayerState;
import server.converter.DataToNetworkConverter;
import server.exceptions.InvalidMapException;
import server.map.*;
import server.mergehalfmaps.MapCreator;

public class Game {
	// private fields
	private String gameId; // unique identifier of this game
	private String gameStateId; // identifier of current game state, pattern: gameId + <counter>
	private boolean gameFinished; // this variable is not needed as movement not included yet
	private List<PlayerState> players; // list of players in this game
	private List<HalfMap> rawHalfMaps; // list of the raw half maps as sent by clients, validated before added here
	private Map currentMap; // the current map of the game
	private HashMap<String, List<EAchievements>> playerAchievements; // phases of the game the players have passed and are in right now
	private String currentTurn; // id of player who is currently set as MustAct
	private int moveCount; // counter of moves that were made by Players
	private int gameStateIdCounter; // counter for gameStateId, increases after every significant action
	private Timestamp lastAction; // timestamp when the last action was conducted, important for 3 sec rule
	
	// ctor for a new game
	public Game(String gameId) {
		this.gameId = gameId;
		this.gameStateIdCounter = 0;
		this.gameStateId = gameId + Integer.toString(gameStateIdCounter); // pattern: increase last number of var by 1 every time significant change occurs
		this.gameFinished = false;
		this.currentMap = new Map();
		this.players = new ArrayList<PlayerState>();
		this.playerAchievements = new HashMap<>();
		this.rawHalfMaps = new ArrayList<HalfMap>();
		this.moveCount = 0;
		this.lastAction = null; // bad error handling
		
		// log the creation of this new game
		Logger.getAnonymousLogger().log(Level.INFO, "New game created, id: " + gameId);
	}
	
	// get id of this game
	public String getGameId() {
		return this.gameId;
	}
	
	// get map of this game
	public Map getMap() {
		return this.currentMap;
	}
	
	// get ids of players of this game
	public List<String> getPlayerIds() {
		List<String> result = new ArrayList<>();
		for(PlayerState player : players) {
			result.add(player.getUniquePlayerID());
		}
		return result;
	}
	
	// get move count of this game
	public int getMoveCount() {
		return this.moveCount;
	}
	
	// get id of players who is currently set on MustAct
	public String getCurrentTurnId() {
		return currentTurn;
	}
	
	// get the timestamp of the last action of this game
	public Timestamp getLastActionTime() {
		return this.lastAction;
	}
	
	// increase the move count of this game by 1
	public void increaseMoveCount() {
		this.moveCount++;
	}
	
	// add a validated half map but check first whether this player has sent a halfmap already
	// if halfmap accepted, switch turn to next player for them to send halfmap or make first move
	// if both maps are received, create the data class game map
	public void addHalfMap(HalfMap halfMap) throws InvalidMapException {
		// check if this player already sent a halfMap
		for(HalfMap currentHalfMap : rawHalfMaps) {
			if(currentHalfMap.getUniquePlayerID().equals(halfMap.getUniquePlayerID())) {
				throw new InvalidMapException("addHalfMap", "This player already sent a halfmap");
			}
		}
		
		// switch Turn to next Player
		switchTurn(currentTurn);
		
		// add halfMap if player hasn't sent one before
		rawHalfMaps.add(halfMap);
		if(rawHalfMaps.size() == 2) {
			currentMap = MapCreator.buildMap(rawHalfMaps);
		}
		increaseGameStateId();
	}
	
	// add a new player to the game
	// if list of players contains two player game will be started and halfmaps will be requested
	public void addPlayer(String firstName, String lastName, String studentId, EPlayerGameState state, String playerId) { // ADD boolean to report success?
		UniquePlayerIdentifier newIdentifier = new UniquePlayerIdentifier(playerId);
		PlayerState newPlayer = new PlayerState(firstName, lastName, studentId, state , newIdentifier, false); // false = treasure not collected
		players.add(newPlayer);
		this.playerAchievements.put(playerId, new ArrayList<EAchievements>());
		if(players.size() == 2) {
				Random random = new Random();
				currentTurn = players.get(random.nextInt(2)).getUniquePlayerID();
				this.lastAction = new Timestamp(System.currentTimeMillis());
		}
		increaseGameStateId();
	}
	
	// takes in the id of the current player on MustAct and switches to the other player
	private void switchTurn(String lastActionPlayerId) {
		for(PlayerState currentPlayer : players) {
			if(!currentPlayer.getUniquePlayerID().equals(lastActionPlayerId)) { currentTurn = currentPlayer.getUniquePlayerID(); }
		}
		this.lastAction = new Timestamp(System.currentTimeMillis());
		increaseGameStateId();
	}
	
	// takes in the id of the player who list this game and sets fields accordingly
	public void setLoser(String loserId) {
		Logger.getAnonymousLogger().log(Level.INFO, loserId + " set as loser of game " + gameId);
		List<PlayerState> newPlayers = new ArrayList<>();
		for(PlayerState playerState : players) {
			UniquePlayerIdentifier tmpIdentifier = new UniquePlayerIdentifier(playerState.getUniquePlayerID());
			if(playerState.getUniquePlayerID().equals(loserId)) {
				PlayerState newPlayerState = new PlayerState(playerState.getFirstName(), playerState.getLastName(), playerState.getStudentID(),
						EPlayerGameState.Lost, tmpIdentifier, playerState.hasCollectedTreasure());
				newPlayers.add(newPlayerState);
			}
			else {
				PlayerState newPlayerState = new PlayerState(playerState.getFirstName(), playerState.getLastName(), playerState.getStudentID(),
						EPlayerGameState.Won, tmpIdentifier, playerState.hasCollectedTreasure());
				newPlayers.add(newPlayerState);
			}
		}
		players = newPlayers;
	}
	
	// increases the gamestateid by 1
	private void increaseGameStateId() {
		gameStateIdCounter++;
		gameStateId = gameId + Integer.toString(gameStateIdCounter);
	}
	
	// create a gamestate for this game that is specifically fitted to the player who requests the gamestate
	// for first 10 moves position of opponent will be hidden, figures will be hidden according to
	// players current game achievements
	// id of opposing player is replaced by a random (fictional) id
	public GameState createPlayerSpecificCurrentGameState(UniquePlayerIdentifier player) {
		// edit playerStates MAYBE PUT THIS IN DIFFERENT CLASS!
		List<PlayerState> playersToBeSent = new ArrayList<>();
		for(PlayerState playerState : players) {
			UniquePlayerIdentifier tmpIdentifier = new UniquePlayerIdentifier(playerState.getUniquePlayerID());
			// check if id should be anonymised
			if(!tmpIdentifier.getUniquePlayerID().equals(player.getUniquePlayerID())) { // this not the player who requested gamestate
				tmpIdentifier = new UniquePlayerIdentifier(UUID.randomUUID().toString()); // give random string as id of this player
			}
			if(playerState.getUniquePlayerID().equals(currentTurn)) { // its this players turn
				EPlayerGameState newPlayerGameState = EPlayerGameState.MustAct;
				if(playerState.getState() == EPlayerGameState.Won || playerState.getState() == EPlayerGameState.Lost) {
					newPlayerGameState = playerState.getState();
				}
				PlayerState newActivePlayerState = new PlayerState(playerState.getFirstName(), playerState.getLastName(), playerState.getStudentID(),
														newPlayerGameState, tmpIdentifier, playerState.hasCollectedTreasure());
				playersToBeSent.add(newActivePlayerState);
			}
			else { // its not this players turn
				EPlayerGameState newPlayerGameState = EPlayerGameState.MustWait;
				if(playerState.getState() == EPlayerGameState.Won || playerState.getState() == EPlayerGameState.Lost) {
					newPlayerGameState = playerState.getState();
				}
				PlayerState newActivePlayerState = new PlayerState(playerState.getFirstName(), playerState.getLastName(), playerState.getStudentID(),
														newPlayerGameState, tmpIdentifier, playerState.hasCollectedTreasure());
				playersToBeSent.add(newActivePlayerState);
			}
		}
		
		// the created map is specifically fitted to the player who requests the gamestate (in terms of figure visibility)
		Optional<FullMap> fullMap = Optional.of(DataToNetworkConverter.mapToFullMap(currentMap, player, playerAchievements, moveCount));
		return new GameState(fullMap, playersToBeSent, gameStateId);
	}
	
}
