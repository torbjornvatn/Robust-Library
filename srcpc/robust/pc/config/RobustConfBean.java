package robust.pc.config;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Konrad Kulakowski
 */
@XStreamAlias("RobustConfiguration")
public class RobustConfBean {
	public static String CONFIG_NAME = "robustcfg.xml";
	
	private String robustFactory;
	/** 
	 * NXT, HEXOR,...
	 */
	private String hardwareArchitecture;
	/** 
	 * e.g. 00:16:53:03:04:E5 is a bluetooth address
	 */
	private String hardwareAddress;	
	/**
	 * possible values: BT, USB, DIRECT
	 */
	private String hardwareTransport;	
	/**
     * This is a calibration parameter. 
     * It describes how long in hardware units is 200 minimeters. 
	 * In case of NXT, it answers the question how degrees must the wheel rotates 
	 * to make vehicle travel 20 centimeters.
     * Unit scale should be taken from calibration procedure.
	 */
	private float unitScale200;
	/**
	 * Distance between wheels in minimeters
	 */
	private int distanceBetweenWheels;
	
	/**
	 * This is calibration parameter which means:
	 * hardware_distance_value*usonicUnitScale=200 (minimeters)
	 * 
	 * In other words sonar should be placed in front of obstacle 
	 * with distance 20 cm and next the parameters should be adjusted according to
	 * the given equation (see above). 
	 */
	private float usonicUnitScale;
	
	public String getRobustFactory() {
		return robustFactory;
	}
	public String getHardwareArchitecture() {
		return hardwareArchitecture;
	}
	public String getHardwareAddress() {
		return hardwareAddress;
	}
	public String getHardwareTransport() {
		return hardwareTransport;
	}
	public float getUnitScale200() {
		return unitScale200;
	}
	public int getDistanceBetweenWheels() {
		return distanceBetweenWheels;
	}
	public float getUsonicUnitScale() {
		return usonicUnitScale;
	}
}