package server.map;

import java.util.Objects;

import MessagesGameState.PlayerState;

// basic data class that represents one of the unique figures, defined by its figureType
public class Figure {
	private FigureType figureType;
	private String owner; // THIS STRING INSTED OF PLAYERSTATE NOW; POTENANTIALLY PROBLEM!!!
	
	public Figure(FigureType f, String player) {
		this.figureType = f;
		this.owner = player;
	}	
	
	public FigureType getFigureType() {
		return this.figureType;
	}
	
	public String getFigureOwner() {
		return this.owner;
	}
	
	public String getFigureOwnerPlayerId() {
		return this.owner;
	}

	@Override
	public int hashCode() {
		return Objects.hash(figureType, owner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Figure other = (Figure) obj;
		return figureType == other.figureType && Objects.equals(owner, other.owner);
	}
	
	@Override
	public String toString() {
		return "Figure: " + this.figureType + " Owner: " + this.owner;
	}

	
	
}
