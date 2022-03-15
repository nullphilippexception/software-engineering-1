package server.game;

import java.util.Random;

// basic class to generate a random unique gameId as specified by the tutorial
public class GameIdGenerator {

	public static String generate() {
		String alphabet[] = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
		Random randomNumberChooser = new Random();
		String result = "";
		
		// get 5 random chars and combine them
		for(int i = 0; i < 5; i++) { 
			int numberOrLetter = randomNumberChooser.nextInt(62); // 10 numbers + 26 small letters + 26 capital letters
			if(numberOrLetter < 10) { // number
				result += Integer.toString(randomNumberChooser.nextInt(10));
			}
			else if(numberOrLetter < 36) { // small letter
				result += alphabet[randomNumberChooser.nextInt(26)];
			} 
			else { // capital letter
				result += alphabet[randomNumberChooser.nextInt(26)].toUpperCase();
			}
		}
		return result;
	}
}
