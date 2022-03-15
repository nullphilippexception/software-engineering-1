package client.network;

import java.util.Optional;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import MessagesBase.ERequestState;
import MessagesBase.HalfMap;
import MessagesBase.PlayerMove;
import MessagesBase.PlayerRegistration;
import MessagesBase.ResponseEnvelope;
import MessagesBase.UniquePlayerIdentifier;
import MessagesGameState.GameState;
import reactor.core.publisher.Mono;

// SOURCE: much of this class was copied/adapted from the MainClient.java given to us
// this class connects the client with the server 
// it sends already into network classes converted (by a different class) data to server and
// receives network classes from the server (but doesn't convert them!)
public class Network { 
	private WebClient baseWebClient;
	private String gameId;
	private String playerId;
	private Logger logger;
	
	// constructor, sets basic fields for game info and creates the WebClient
	public Network(String serverBaseUrl, String gameId) {
		this.logger = LoggerFactory.getLogger(Network.class); // logger for this class
		this.gameId = gameId;
		this.playerId = "NOT ASSIGNED YET";
		
		baseWebClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE) //the network protocol uses XML
			    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
			    .build();
	}

	// registers client with the server and in turn receives the UniquePlayerId of this client for this game
	// also sends personal data to server to connect client to author of this program
	public String playerRegistration() {
		PlayerRegistration playerReg = new PlayerRegistration("Philipp", "S", ""); // personal data, partially removed in GitHub code for privacy reasons
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST)
				.uri("/" + gameId + "/players")
				.body(BodyInserters.fromValue(playerReg)) // specify the data which is sent to the server
				.retrieve()
				.bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();
	
		if(resultReg.getState() == ERequestState.Error) {
			logger.error("Could not get a player Id, game will not be able to be played");
			return "Registration failed"; // default return for failed registration
		}
		else { // registration was succesful
			UniquePlayerIdentifier uniqueID = resultReg.getData().get();
			playerId = uniqueID.getUniquePlayerID();
			return uniqueID.getUniquePlayerID();
		}
	}
	
	// sends HalfMap network class to server
	public void sendHalfMap(HalfMap halfMap) {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST)
				.uri("/" + gameId + "/halfmaps")
				.body(BodyInserters.fromValue(halfMap)) // specify the data which is sent to the server
				.retrieve()
				.bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope resultReg = webAccess.block();
	
		// act according to server reaction
		if(resultReg.getState() == ERequestState.Error) {
			logger.error("HalfMap was not sent to server successfully, can't start game");
		}
		else {
			logger.info("HalfMap was sent to server successfully");
		}
	}
	
	// Requests current GameState from server
	public Optional<GameState> getGameState() {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.GET)
				.uri("/" + gameId + "/states/" + playerId)
				.retrieve()
				.bodyToMono(ResponseEnvelope.class); // specify the object returned by the server
		
		ResponseEnvelope<GameState> requestResult = webAccess.block();
	
		// act according to server reaction
		if(requestResult.getState() == ERequestState.Error) {
			logger.error("Unable to receive GameState, Error: " + requestResult.getExceptionMessage());
			return Optional.empty();
		}
		else {
			Optional<GameState> result = requestResult.getData();
			return result;
		}
	}
	
	// Send Movement of the avatar of this client to the server
	public void sendMovement(PlayerMove playerMove) {
		Mono<ResponseEnvelope> webAccess = baseWebClient.method(HttpMethod.POST)
				.uri("/" + gameId + "/moves")
				.body(BodyInserters.fromValue(playerMove)) // specify the data which is sent to the server
				.retrieve()
				.bodyToMono(ResponseEnvelope.class); // specify the object returned by the server

		ResponseEnvelope resultReg = webAccess.block();
	
		if(resultReg.getState() == ERequestState.Error) {
			logger.error("Movement was not successfully sent to server");
		}
		else {
			// logging every succesful movement would spam the logs, as long as there is no error the move was fine
		}
	}
		
}
