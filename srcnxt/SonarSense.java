import java.util.Vector;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * @author Konrad Kulakowski
 */
public class SonarSense {
	private UltrasonicSensor usonic = null;
	private Motor motor = null;
	private Vector eventActions = new Vector();

	class SonarEventAction implements EventAction {
		private int methodUID;
		private int mode;
		private int alarmDistance;
		private boolean active;

		public SonarEventAction(int methodUID, int mode, int alarmDistance) {
			this.methodUID = methodUID;
			this.mode = mode;
			this.alarmDistance = alarmDistance;
		}
		
		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public int getMode() {
			return mode;
		}

		public int getAlarmDistance() {
			return alarmDistance;
		}

		public void doAction(int[] actionParams) {
			NXTBTMsg btMsg = new NXTBTMsg(methodUID,
					NXTMessageType.MSGTYPE_ASC_SONAR_LISTENER, actionParams);
			RobustNXT.sendBTMsg(btMsg);
		}
	}

	public SonarSense(SensorPort ultrasonicPort, Motor motor) {
		usonic = new UltrasonicSensor(ultrasonicPort);
		this.motor = motor;

	}
	
	public boolean exist() {
		if ( ! "        ".equals(usonic.getSensorType()) ) {
			return true;
		} else {
			return false;
		}			
	}
	

	public void init() {
		if (usonic.reset() != 0)
			LoggerNXT.error("Sonar reset failed");

		if (usonic.capture() != 0)
			LoggerNXT.error("Sonar set capture mode failed");

		this.motor.resetTachoCount();
		this.motor.setSpeed(200);
		this.motor.regulateSpeed(true);

		SonarObserver observer = new SonarObserver();
		observer.start();
		LoggerNXT.info("sonar initialized...");
	}

	public void setSonarTarget(NXTBTMsg msg) {
		motor.rotateTo(msg.args[0], true);
	}

	public void getSonarTarget(int methodUID) {
		RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
				NXTMessageType.MSGTYPE_SONAR_GET_TARGET, new int[] { motor
						.getTachoCount() }));
	}

	public void getDistance(int methodUID) {
		RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
				NXTMessageType.MSGTYPE_SONAR_GET_DISTANCE, new int[] { usonic
						.getDistance() }));
	}

	public void registerProximityAlarmListener(NXTBTMsg msg) {
		// msg.args[0] - mode
		// msg.args[1] - alarm distance
		eventActions.insertElementAt(new SonarEventAction(msg.methodUID,
				msg.args[0], msg.args[1]), eventActions.size());
	}

	public void addEventAction(SonarEventAction action) {
		eventActions.insertElementAt(action, eventActions.size());
	}

	class SonarObserver extends Thread {
		int[] buffer = new int[1];

		public void run() {
			int previousDistance = -1; // -1 means unset
			while (true) {
				int currentDistance = usonic.getDistance();
				if (previousDistance == -1) {
					previousDistance = currentDistance;
					continue;
				}
				buffer[0] = currentDistance;

				if (previousDistance == currentDistance) {
					// nothing to update
					continue;
				}

				for (int i = 0; i < eventActions.size(); i++) {
					final SonarEventAction action = (SonarEventAction) eventActions
							.elementAt(i);
					
					if (action.getMode() == 0) {
						action.doAction(buffer);
					} else if (action.getMode() == 1) {
						if (currentDistance < action.getAlarmDistance()
								&& action.isActive()) {
							action.setActive(false);
							action.doAction(buffer);
						} else if (currentDistance >= action.getAlarmDistance()) {
							action.setActive(true);
						}
					} else if (action.getMode() == 2) {
						if (currentDistance > action.getAlarmDistance()
								&& action.isActive()) {
							action.setActive(false);
							action.doAction(buffer);
						} else if (currentDistance <= action.getAlarmDistance()) {
							action.setActive(true);
						}
					} else if (action.getMode() == 3) {
						LoggerNXT.info("al:" + action.getAlarmDistance() + ", cur: " + currentDistance);						
						if (currentDistance < action.getAlarmDistance()) {
							action.doAction(buffer);
						}
					} else if (action.getMode() == 4) {
						if (currentDistance >= action.getAlarmDistance()) {
							action.doAction(buffer);
						}
					}
				} // for
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				previousDistance = currentDistance;
			} // while
		} // run
	} // SonarObserver
}