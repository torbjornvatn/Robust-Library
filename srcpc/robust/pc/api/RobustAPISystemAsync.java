package robust.pc.api;

/**
 * This interface allows for logging messages, which are sent from the body
 * @author Konrad Kulakowski
 */
public interface RobustAPISystemAsync extends RobustAPI {
	public static interface BodyLoggerHandler {
		void handleLogEvent(String msg);
	}
	
	void registerLogEvent(BodyLoggerHandler handler);
}