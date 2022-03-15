package client.controller;

// enum that steers the artificial intelligence of the client
// the AI sets different actions dependent on the current game phase
public enum EGamePhase {
	FIND_TREASURE (true),
	GET_TREASURE (true),
	GET_OPP_SITE (true),
	FIND_CASTLE (true),
	GET_CASTLE (true);
	
	boolean isNew;
	
	private EGamePhase(boolean isNew) {
		this.isNew = isNew;
	}
	
	public void setIsNew() {
		this.isNew = true;
	}
	
	public void setIsOld() {
		this.isNew = false;
	}
	
	public boolean getIsNew() {
		return this.isNew;
	}
}
