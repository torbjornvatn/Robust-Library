package robust.pc.api;

/**
 * @author Konrad Kulakowski
 */
public interface RobustAPISonarAsync extends RobustAPI {
	/**
	 * Allows for asynchronous distance change handling
	 */
	public interface ProximityAlarmListner {
		void handleProximityAlarm(int distance);
		/**
		 * Allows for setting alarm distance
		 * @return
		 */
		int getAlarmDistance();
		/**
		 * mode 0 - inform on change
		 * mode 1 - inform if object become closer than alarm distance
		 * mode 2 - inform if object become further than alarm distance
		 * mode 3 - keep notifying if object become closer than alarm distance
		 * mode 4 - keep notifying if object become further than alarm distance
		 * @return mode number
		 */
		int getMode();
	}
	
	void registerProximityAlarmListener(ProximityAlarmListner listener);
}