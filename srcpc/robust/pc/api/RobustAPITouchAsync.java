package robust.pc.api;

/**
 * Allow for getting information about changing state of touch sense
 * asynchronously
 * 
 * @author Konrad Kulakowski
 */
public interface RobustAPITouchAsync extends RobustAPI {
	public interface TouchListener {
		/**
		 * @param touchStates
		 *            first cell describes state of first touch sensor, second
		 *            cell is for second touch sensor etc... Integers values
		 *            also allow for expressing force of touch. If the given
		 *            hardware allows for this.
		 */
		void handleTouchEvent(int[] touchStates);
	}

	public void registerTouchListener(TouchListener touchListener);
}