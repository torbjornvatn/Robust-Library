package robust.pc.api;

/**
 * API for ultrasonic sensor mounted on rotable column 
 * 
 * @author Konrad Kulakowski
 */
public interface RobustAPISonar extends RobustAPI {
	/**
	 * Rotates sonar towards obstacle on the given degs
	 * @param degs
	 */
	void setSonarTarget(int degs);
	/**
	 * Return actual sonar position
	 * @return
	 */
	int getSonarTarget();
	/**
	 * return distance to obstaclew
	 * @return
	 */
	int getDistance();
}