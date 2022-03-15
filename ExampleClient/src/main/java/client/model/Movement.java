package client.model;

// basic movement data class
public class Movement {
	MovementType movementType; // direction in which the movement is supposed to happen
	
	public Movement(MovementType direction) {
		this.movementType = direction;
	}
	
	public MovementType getMovementType() {
		return this.movementType;
	}
}
