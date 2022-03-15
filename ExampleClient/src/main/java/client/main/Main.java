package client.main;

import client.controller.MainClient;
import client.model.Map;
import client.view.*;

// this is the main class of my project
public class Main {
	
	// SOURCE: My MVC pattern is based on the implementation presented in the tutorial by Dr. Böhmer
	public static void main(String[] args) {
		Map map = new Map(); // MODEL
		CommandLineView commandLineView = new CommandLineView(map); // VIEW
		MainClient mainClient = new MainClient(args, map); // CONTROLLER
		
		mainClient.play(); // start executing the game
	}

}
