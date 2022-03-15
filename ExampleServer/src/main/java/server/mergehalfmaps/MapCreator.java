package server.mergehalfmaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import MessagesBase.ETerrain;
import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import server.map.Coordinates;
import server.map.Figure;
import server.map.FigureType;
import server.map.Map;
import server.map.MapElement;
import server.map.MapElementType;

// this class converts two halfmaps into one data class map
// the halfmaps have been checked on validity before this class is called
public class MapCreator {
	
	// main method to fuse the halfmaps and convert them into data class map
	public static Map buildMap(List<HalfMap> halfMaps) {
		HashMap<Coordinates, MapElement> elements = new HashMap<>();
		
		// randomly determine how maps will be put together
		Random random = new Random();
		int chosenMergeFormat = random.nextInt(4);
		HashMap<Integer, HashMap<String, Integer>> offsets = determineOffset(chosenMergeFormat);
		int playerCount = 1;
		
		// iterate through list of maps (contains two elements)
		for(HalfMap currentHalf : halfMaps) {
			int treasureElementX = random.nextInt(8); // randomly determine where treasure will be placed
			int treasureElementY = random.nextInt(4);
			
			// check whether this element is eligible for treasure -> if not, get new element
			boolean searchTreasurePlace = true;
			while(searchTreasurePlace) {
				if(getHalfMapElementByCoordinates(currentHalf, new Coordinates(treasureElementX, treasureElementY))
						.getTerrain().equals(ETerrain.Grass) &&
						!getHalfMapElementByCoordinates(currentHalf, new Coordinates(treasureElementX, treasureElementY))
						.isFortPresent()) {
					searchTreasurePlace = false;
				}
				else {
					treasureElementX = random.nextInt(8);
					treasureElementY = random.nextInt(4);
				}
			}
			
			String currentPlayer = currentHalf.getUniquePlayerID();
			// get all the halfmapnodes and convert them
			for(HalfMapNode node : currentHalf.getMapNodes()) {
				Coordinates currentCoord = new Coordinates(node.getX(), node.getY());
				MapElementType currentType = convertETerrainToMapElementType(node.getTerrain()).get();
				List<Figure> currentFigures = new ArrayList<>();
				if(node.isFortPresent()) {
					// add figure and castle for this player
					Figure avatar = new Figure(FigureType.AVATAR, currentPlayer);
					Figure castle = new Figure(FigureType.CASTLE, currentPlayer);
					currentFigures.add(avatar);
					currentFigures.add(castle);
				}
				if(node.getX() == treasureElementX && node.getY() == treasureElementY) { 
					// element where treasure should be placed is reached
					Figure treasure = new Figure(FigureType.TREASURE, currentPlayer);
					currentFigures.add(treasure);
				}
				MapElement newElement = new MapElement(currentType, currentFigures);
				Coordinates newCoord = new Coordinates(currentCoord.getX() + offsets.get(playerCount).get("x"),
														currentCoord.getY() + offsets.get(playerCount).get("y"));
				elements.put(newCoord, newElement);
			}
			playerCount++;
		}
		
		return new Map(elements);
	}
	
	// this method computes the offsets for each element on the map, this is needed because
	// the halfmaps are randomly put together
	// using this method there is no need in buildMap to account for these different possibilities
	private static HashMap<Integer, HashMap<String, Integer>> determineOffset(int decision) {
		HashMap<Integer, HashMap<String, Integer>> result = new HashMap<>();
		List<Integer> offsetValues = new ArrayList<>();
		
		if(decision == 0) { // ONE IS LEFT
			int[] offsets = {0,0,8,0};
			for(int i : offsets) {
				offsetValues.add(i);
			}
		}
		if(decision == 1) { // ONE IS RIGHT
			int[] offsets = {8,0,0,0};
			for(int i : offsets) {
				offsetValues.add(i);
			}
		}
		if(decision == 2) { // ONE IS UP
			int[] offsets = {0,0,0,4};
			for(int i : offsets) {
				offsetValues.add(i);
			}
		}
		if(decision == 3) { // ONE IS DOWN
			int[] offsets = {0,4,0,0};
			for(int i : offsets) {
				offsetValues.add(i);
			}
		}
		
		// create hashmaps with needed values
		HashMap<String, Integer> valuesForOne = new HashMap<>();
		valuesForOne.put("x", offsetValues.get(0));
		valuesForOne.put("y", offsetValues.get(1));
		
		HashMap<String, Integer> valuesForTwo = new HashMap<>();
		valuesForTwo.put("x", offsetValues.get(2));
		valuesForTwo.put("y", offsetValues.get(3));
		
		// add hashmaps to final result hashmap
		result.put(1, valuesForOne);
		result.put(2, valuesForTwo);
		
		return result;
	}
	
	// helper method that converts ETerrain (network enum) into MapElementType (local enum)
	private static Optional<MapElementType> convertETerrainToMapElementType(ETerrain eterrain) {
		if(eterrain.equals(ETerrain.Water)) return Optional.of(MapElementType.WATER);
		if(eterrain.equals(ETerrain.Grass)) return Optional.of(MapElementType.GRASS);
		if(eterrain.equals(ETerrain.Mountain)) return Optional.of(MapElementType.MOUNTAIN);
		else { // input was an unknown ETerrain type
			Logger logger = LoggerFactory.getLogger(MapCreator.class);
			logger.warn("Tried to convert an unknown ETerrain: " + eterrain);
			return Optional.empty(); 
		}
	}
	
	// helper method to find a halfmapnode by its coordinates
	private static HalfMapNode getHalfMapElementByCoordinates(HalfMap halfMap, Coordinates targetCoord) {
		for(HalfMapNode halfMapNode : halfMap.getMapNodes()) {
			Coordinates currentCoord = new Coordinates(halfMapNode.getX(), halfMapNode.getY());
			if(currentCoord.equals(targetCoord)) return halfMapNode;
		}
		Logger logger = LoggerFactory.getLogger(MapCreator.class);
		logger.warn("Asked for non existing HalfMapNode");
		return new HalfMapNode(); // BAD EXCEPTION HANDLING!
	}
}
