package server.halfmapvalidator;

import java.util.HashSet;
import java.util.Set;

import MessagesBase.HalfMap;
import MessagesBase.HalfMapNode;
import server.businessrules.IMapRule;
import server.exceptions.InvalidMapException;
import server.map.Coordinates;

// this validator ensures that the received map is in a 4x8 format with the top left field having the coordinates (0|0)
public class MapFormatValidator implements IMapRule {

	public void checkRule(HalfMap map) throws InvalidMapException {
		Set<Coordinates> referenceCoordinatesSet = new HashSet<>();
		Set<Coordinates> receivedCoordinates = new HashSet<>();
		
		// build reference set of expected coordinates
		int X_AXIS_LENGTH = 8;
		int Y_AXIS_LENGTH = 4;
		for(int x = 0; x < X_AXIS_LENGTH; x++) {
			for(int y = 0; y < Y_AXIS_LENGTH; y++) {
				Coordinates newCoordinates = new Coordinates(x,y);
				referenceCoordinatesSet.add(newCoordinates);
			}
		}
		
		// extract coordinates from received map
		for(HalfMapNode node : map.getMapNodes()) {
			Coordinates nodeCoordinates = new Coordinates(node.getX(), node.getY());
			receivedCoordinates.add(nodeCoordinates);
		}
		
		// compare sets
		if(!referenceCoordinatesSet.equals(receivedCoordinates)) {
			throw new InvalidMapException("MapFormatValidator", "Received map not in right format"); // CREATE NEW EXCEPTIONS!
		}
	}
}
