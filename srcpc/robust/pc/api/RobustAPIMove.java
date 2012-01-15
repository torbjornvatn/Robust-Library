package robust.pc.api;

/**
 * @author Konrad Kulakowski
 */
public interface RobustAPIMove extends RobustAPI {

	public enum MoveInfo {
		STOP(0), FORWARD_MOVE(1), SMOOTH_TURN(2), INPLACE_TURN(3);

		MoveInfo(int code) {
			this.code = code;
		}

		public static MoveInfo init(MoveInfo t, int speed, int length,
				int radius, int turnDegs, int percent) {
			t.speed = speed;
			t.length = length;
			t.radius = radius;
			t.turnDegs = turnDegs;
			t.percent = percent;
			return t;
		}

		public static MoveInfo getByCode(int code) {
			switch (code) {
			case 0:
				return STOP;
			case 1:
				return FORWARD_MOVE;
			case 2:
				return SMOOTH_TURN;
			case 3:
				return INPLACE_TURN;
			default:
				return null;
			}
		}

		private int code, speed, length, turnDegs, radius, percent;

		public int code() {
			return code;
		}

		public int getSpeed() {
			return speed;
		}

		public int getLength() {
			return length;
		}

		public int getRadius() {
			return radius;
		}

		public int getTurnDegs() {
			return turnDegs;
		}

		public int getPercentOfExecution() {
			return percent;
		}
	}

	/**
	 * Add to route forward move with length given by units and speed given in
	 * units per second by speed. Note that command is not executed immediately,
	 * but it is just queued to be run in the future;
	 * 
	 * @param lenght
	 *            - number of millimeters to go forward
	 * @param speed
	 *            - how much millimeters per second
	 * @param keepAzimuth
	 *            - this allow to control certain azimuth (which make sense only
	 *            if vehicle has a magnetic compass or giro) keepAzimuth == -1
	 *            means tell the body does not use the compass or giroscope
	 */
	public abstract void moveForward(int lenght, int speed, int keepAzimuth);

	/**
	 * This move is a smooth turn to right or left according to certain radius
	 * 
	 * @param turnDegrees
	 *            - e.g. 90 means turn right about 90 degrees -90 means turn
	 *            left.
	 * @param radius
	 *            - radius of turn in millimeters
	 * @param speed
	 *            - speed of the external wheel in millimeters per second
	 */
	public abstract void moveCircly(int turnDegrees, int radius, int speed);

	/**
	 * This move is a turn left or right in place. I.e. there is an axis of move
	 * that stays constant during the turn
	 * 
	 * @param turnDegrees
	 * @param speed
	 */
	public abstract void moveTurnInPlace(int turnDegrees, int speed);

	/**
	 * This is command: stop as soon as possible. It is a nonblocking call
	 */
	public abstract void stopNow();

	/**
	 * Allow to finish current move, but move queue gets cleared
	 */
	public abstract void stopMoving();

	/**
	 * This waits as long as state of move get change It returns parameters of
	 * the new move thats just began
	 * 
	 * @return
	 */
	public abstract MoveInfo waitForMoveChange();

	/**
	 * It returns array [type of move, percent_of_execution] contains types
	 * MoveInfo, int,
	 * 
	 * @return
	 */
	public abstract MoveInfo moveStatus();
}