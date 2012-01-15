import lejos.nxt.addon.CompassSensor;
import lejos.nxt.I2CPort;
import lejos.nxt.Motor;

/**
 * @author Konrad Kulakowski
 */
public class CompassSense extends CompassSensor {
	byte[] command = new byte[2];

	public CompassSense(I2CPort port) {
		super(port);
		resetCartesianZero();
	}
	/**
	 * This works for standard two whiled vehicle, such as TriBot.
	 * For other constructions, different algorithm has to be provided.
	 * @param left
	 * @param right
	 */
	public void calibrateCompass(Motor left, Motor right) {
		startCalibration();
		left.setSpeed(50);
		right.setSpeed(50);
		left.regulateSpeed(true);
		right.regulateSpeed(true);
		right.rotate(-2000);
		stopCalibration();
	}

	float getAbsoluteDegreesCartesian() {
		return 360 - getDegrees();
	}
}