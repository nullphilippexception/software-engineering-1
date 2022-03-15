package server.businessrules;

import java.sql.Timestamp;
import server.exceptions.TimeLimitException;

// validator that checks whether the given lastAction is within three seconds of the current time
public class TimeValidator {
	public static void checkThreeSecondsRule(Timestamp lastAction) throws TimeLimitException {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		if(currentTime.getTime() - lastAction.getTime() > 3000 && !lastAction.equals(null)) {
			throw new TimeLimitException("TimeValidator", "Client took too much time");
		}
	}
}
