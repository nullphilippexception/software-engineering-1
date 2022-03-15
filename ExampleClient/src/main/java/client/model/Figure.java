package client.model;

import java.util.Objects;

// basic data class that represents one of the unique figures, defined by its figureType
public class Figure {
	private FigureType figureType;
	
	public Figure(FigureType f) {
		this.figureType = f;
	}	
	
	public FigureType getFigureType() {
		return this.figureType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(figureType);
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
		return figureType == other.figureType;
	}
	
	
}
