package robust.pc.api;

/**
 * @author Konrad Kulakowski
 */
public interface RobustAPICompass extends RobustAPI {
	/**
	 * returns actual azimuth according to Compass (range 0 - 359) 
	 */
	public abstract int getCompassDegs();
	
	/**
	 * returns actual position according to Compass 
	 */
	public abstract void calibrateCompass();	
	
	/**
	 * Set the current azimuth to zero
	 */
	public abstract void resetCompass();
}
