package client.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesGameState.EPlayerGameState;
import MessagesGameState.GameState;
import client.mapcreation.MapGenerator;
import client.mapcreation.MapValidator;
import client.model.Coordinates;
import client.model.FigureType;
import client.model.Map;
import client.model.MapElement;
import client.model.MapElementType;
import client.network.DataToNetworkConverter;
import client.network.Network;
import client.network.NetworkToDataConverter;
import client.view.CommandLineView;

// main CONTROLLER of the MVC pattern
// this class ties the whole project together, it is responsible for executing the whole program
// it contains one public method, play(), which is called by Main.java, the class that receives the starting parameters
// play() then steers the sequence of actions by subsequently calling other (private) methods that fulfill specific roles 
public class MainClient {
	Map gameMap; // the map the game is played on
	String[] args; // starting parameters, received from Main.java
	String GAME_MODE; 
	String SERVER_URL;
	String GAME_ID;
	String PLAYER_ID;
	Network network;
	Logger logger;
	GameState gameState; // the current state of the game as communicated by the server
	boolean treasureFound;
	PathAnalyzer pathAnalyzer; // an instance of a pathAnalyzer, that steers the movement; will be initialized with a factory pattern
	List<Coordinates> visitedCoordinates; // list of coordinates the client has already visited
	
	// ctor
	public MainClient(String[] args, Map map) {
		this.args = args; 
		this.gameMap = map;
		this.logger = LoggerFactory.getLogger(MainClient.class);
		
		// get starting arguments
		GAME_MODE = "TEST"; 
		SERVER_URL = ""; // URL of the SE1 testing server, removed for privacy reasons in GitHub code
		GAME_ID = "asKhH"; // INSERT GAME ID HERE
		PLAYER_ID = "empty"; // default till id is requested
		if(args.length == 3) { // if start parameters were provided
			GAME_MODE = args[0];
			SERVER_URL = args[1];
			GAME_ID = args[2];
		}
		
		// create network object for server interaction
		network = new Network(SERVER_URL, GAME_ID);
		
		// create visited Coordinates list
		visitedCoordinates = new ArrayList<>();
	}

	// main method of the class and only public method
	// steers the game by calling private methods that fulfill specific tasks
	public void play() {		
		// Register client with server
		PLAYER_ID = network.playerRegistration();
		logger.info("Player ID: " + PLAYER_ID);
					
		//wait to be allowed to interact, then generate map and send it
		waitForPossibilityToSendMap(); // loop of getting gamestate until gamestate includes MustAct for this client
		sendMapToServer(); // sends this clients half map to server
					
		// create full map as local data class
		createFullMap(); // sets gameMap field
		
		// set PathAnalyzer using factory pattern
		pathAnalyzer = PathAnalyzerFactory.getPathAnalyzer(gameMap);
					
		// set game-steering and info variables
		treasureFound = false; 
		int countMyMoves = 1; // will be used to know when to compute the castleArea of the opponent
			
		// play game until one player wins
		logger.info("Pathfinding begins: ");
		while(true) { // will be ended by exit from program once a player has won
			countMyMoves = moveOnMap(countMyMoves); // moveOnMap receives the gameState and sends moves in response to it
		}
	}
	
	// responsible for the playing part of the game
	// receives gameStates from the server, calls pathAnalyzer to compute response and sends movement
	// uses other private methods to determine phase of game and whether a player already won
	private int moveOnMap(int countMyMoves) {
		// Log current round for easier comparsion to view round counts
		logger.info("CURRENT ROUND: [#" + (countMyMoves) + "]");
	
		// wait for GameState to be ready, then set gameMap with it and subsequently update PathAnalyzer
		gameState = waitForGameStateToBeReady(); // returns gameState once its either MustAct for this client or someone won
		setGameMap();
		pathAnalyzer.setMap(gameMap); 
		
		// check if game is over already
		hasOnePlayerWon(); // ends program (after notifying view of win) if one player has won
		
		// set gamephase -> REMOVED ELSE IF
		determineGameState(); // uses pathanalyzers setGamePhase method to tell it the current phase of the game
		
		// mark current coordinates as visited
		markVisitedCoordinates();
		for(Coordinates coord : visitedCoordinates) { pathAnalyzer.markVisitedCoordinates(coord); }
				
		// send Movement
		network.sendMovement(DataToNetworkConverter.movementToPlayerMove(pathAnalyzer.move(), PLAYER_ID));
		
		// determine opponent castle area once their true player position is available (6 to avoid first/second mover distinction)
		if(countMyMoves == 6) {
			pathAnalyzer.determineOpponentCastleArea();
		}
		
		// return next round number
		return countMyMoves + 1;
	}
	
	// iterates through a loop until a valid game state is received
	private void waitForPossibilityToSendMap() {
		boolean actionAllowed = false;
		while(!actionAllowed) {
				GameState currentState = network.getGameState().get();
				if(!currentState.equals(null) && NetworkToDataConverter.checkIfMyTurn(currentState, PLAYER_ID)) {
					actionAllowed = true;
				}
		}
	}
	
	// generates maps until a valid one is found and then sends it to the server
	private void sendMapToServer() {
		Map myHalfMap = new Map(new HashMap<Coordinates, MapElement>());
		do {
			myHalfMap = MapGenerator.generateMap();
		} while(!MapValidator.isMapValid(myHalfMap));
					
		network.sendHalfMap(DataToNetworkConverter.mapToHalfMap(myHalfMap, PLAYER_ID));
	}
	
	// extracts a full map from the received game state and checks whether it is valid
	private void createFullMap() {
		GameState gameStateForMapCreation = network.getGameState().get();
		boolean functionalMap = false;
		do { // loop until a functional map is received
			try {
				functionalMap = true;
				gameStateForMapCreation = network.getGameState().get();
				TimeUnit.MILLISECONDS.sleep(500);
				
				// try if this map works, will throw NPE otherwise
				gameMap.setMap(NetworkToDataConverter.getMapFromGameState(gameStateForMapCreation).get()); // this sets the acutal game map
				MapElementType testIfMapContainsContent = gameMap.getElementByCoordinates(new Coordinates(0,0)).getMapElementType();
			}
			catch(Exception e) { // not too great using exception for flow control, but it works...
				logger.warn("Creating full map was unsuccessful");
				functionalMap = false;
			}

		} while(!functionalMap);
	}
	
	// contains a loop that continues until a valid game state is received that either contains the information that
	// one player has won the game or that this client must act now
	private GameState waitForGameStateToBeReady() {
		boolean moveAllowed = false;
		GameState gameState = null;
		while(!moveAllowed) {
				GameState currentState = network.getGameState().get(); // EXCEPTION HANDLING?
				if(!currentState.equals(null) && (NetworkToDataConverter.checkIfMyTurn(currentState, PLAYER_ID)
					|| NetworkToDataConverter.getPlayerGameState(currentState, PLAYER_ID).get().getState().equals(EPlayerGameState.Won)
					|| NetworkToDataConverter.getPlayerGameState(currentState, PLAYER_ID).get().getState().equals(EPlayerGameState.Lost))) {
					moveAllowed = true;
					gameState = currentState;
				}
		}
		return gameState;
	}
	
	// sets the game map based on the received game state
	private void setGameMap() {
		try { gameMap.setMap(NetworkToDataConverter.getMapFromGameState(gameState).get()); }
		catch(Exception e) { logger.warn(e.toString()); }
	}
	
	// checks whether one player has won the game by converting and comparing the current PlayerGameState of this player
	private void hasOnePlayerWon() {
		if(NetworkToDataConverter.getPlayerGameState(gameState, PLAYER_ID).get().getState() == EPlayerGameState.Won) {
			gameMap.setWinner(true); // notifies View
			try {
				TimeUnit.MILLISECONDS.sleep(500);
				System.exit(0);
			}
			catch(Exception e) { logger.error(e.toString()); }
		}
		if(NetworkToDataConverter.getPlayerGameState(gameState, PLAYER_ID).get().getState() == EPlayerGameState.Lost) {
			gameMap.setWinner(false); // notifies View
			try {
				TimeUnit.MILLISECONDS.sleep(500);
				System.exit(0);
			}
			catch(Exception e) { logger.error(e.toString()); }
		}
	}
	
	// determines the current game state by checking various criteria
	// doesn't need a return type because game state is directly passed to pathAnalyzer which is a field of this class
	private void determineGameState() {
		// check if FIND_TREASURE is the current phase
		if(gameMap.findThisFigure(FigureType.MY_TREASURE).equals(new Coordinates(-1,-1)) && !treasureFound) {
			pathAnalyzer.setGamePhase(EGamePhase.FIND_TREASURE);
		}
		
		// check if GET_TREASURE is the current phase
		if(!gameMap.findThisFigure(FigureType.MY_TREASURE).equals(new Coordinates(-1,-1)) && !treasureFound) {
			if(EGamePhase.GET_TREASURE.getIsNew()) { EGamePhase.GET_TREASURE.setIsOld(); } 
			pathAnalyzer.setGamePhase(EGamePhase.GET_TREASURE);
		}
		
		// check if treasure has been found and GET_OPP_SITE is now the phase
		if(NetworkToDataConverter.getPlayerGameState(gameState, PLAYER_ID).get().hasCollectedTreasure() && !treasureFound) {
			treasureFound = true;
			Coordinates treasureCoordinates = gameMap.findThisFigure(FigureType.MY_TREASURE);
			if(EGamePhase.GET_OPP_SITE.getIsNew()) { EGamePhase.GET_OPP_SITE.setIsOld(); }
			
			if(!treasureCoordinates.equals(new Coordinates(-1,-1))) { // (-1,-1) are the default coordinates for figures that are not on the map
				gameMap.getElementByCoordinates(treasureCoordinates).removeFigure(FigureType.MY_TREASURE); 
				pathAnalyzer.treasureFound();
			}
			pathAnalyzer.setGamePhase(EGamePhase.GET_OPP_SITE);
		} 
		
		// check if GET_CASTLE is the current phase
		if(!gameMap.findThisFigure(FigureType.OPPONENT_CASTLE).equals(new Coordinates(-1,-1)) && treasureFound) {
			if(EGamePhase.GET_CASTLE.getIsNew()) { EGamePhase.GET_CASTLE.setIsOld(); }
			pathAnalyzer.setGamePhase(EGamePhase.GET_CASTLE);
		} 
	}
	
	// marks visited coordinates, including the adjacent nodes if the current field is a mountain
	private void markVisitedCoordinates() {
		// check if this field has been visited before
		// if not, make new entry in visitedCoordinates, if yes
		// also enter all the fields that can be seen from current field it is a mountain
		Coordinates avatarLocation = gameMap.findThisFigure(FigureType.MY_AVATAR);
		if(gameMap.getElementByCoordinates(avatarLocation).getMapElementType().equals(MapElementType.MOUNTAIN)) {
			List<Coordinates> inMountainView = MapAnalyzer.getFieldsInMountainView(gameMap);
			for(Coordinates coord : inMountainView) {
				if(!visitedCoordinates.contains(coord)) visitedCoordinates.add(coord);
			}
		}
		if(!visitedCoordinates.contains(avatarLocation))visitedCoordinates.add(avatarLocation); // because inMountainView does not include mountain itself
	}
	
}
