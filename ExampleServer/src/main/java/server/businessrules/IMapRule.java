package server.businessrules;

import MessagesBase.HalfMap;
import server.exceptions.GenericExampleException;
import server.exceptions.InvalidMapException;

// interface that is implemented by the classes from server.halfmapvalidator package
public interface IMapRule {
	public void checkRule(HalfMap map) throws InvalidMapException;
}
