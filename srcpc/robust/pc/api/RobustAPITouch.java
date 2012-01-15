package robust.pc.api;

/**
 * @author Konrad Kulakowski
 */
public interface RobustAPITouch extends RobustAPI {
	/**
	 * Returns vector describing touch sense state of all connected sensors
	 * In case of Mindstorms this will be a one cell lenght vector of ints containing
	 * 0 or 1, where 0 means not pressed, 1 pressed. 
	 * In case of Hexor it might returns current state of whiskers as a vector of two ints etc.
	 * @return
	 */
	public int[] getTouchSenseState();
}