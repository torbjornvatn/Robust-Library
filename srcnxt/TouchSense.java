import java.util.Vector;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

/**
 * @author Konrad Kulakowski
 */
public class TouchSense {
	private int[] isPressed = new int[1];
	private TouchSensor sensor = null;
	private Vector eventActions = new Vector();

	class SendMsgEventAction implements EventAction {
		public void doAction(int[] actionParams) {
			NXTBTMsg btMsg = new NXTBTMsg(NXTMessageType.MSGTYPE_ASC_TOUCH_SENSE,
					NXTMessageType.MSGTYPE_ASC_TOUCH_SENSE, actionParams);
			RobustNXT.sendBTMsg(btMsg);
		}
	}
	
	public TouchSense (SensorPort sensorPort) {
		sensor = new TouchSensor(sensorPort);
		isPressed[0] = 0; // which means it is not pressed
		// probably except this standard action there might be some connected with
		// reflexes
		eventActions.insertElementAt(new SendMsgEventAction(), eventActions.size());
		TouchObserver observer = new TouchObserver();
		observer.start();
	}
	
	public void addEventAction(EventAction action) {
		eventActions.insertElementAt(action, eventActions.size());
	}
	
	public boolean isPressed() {
		return sensor.isPressed();
	}
	
	class TouchObserver extends Thread {
		public void run() {
			int wasPressed = 0;
			while (true) {
				isPressed[0] = (sensor.isPressed()?1:0);
				if ( isPressed[0] - wasPressed != 0) { // state has been changed
					for (int i = 0; i < eventActions.size(); i++) {
						((EventAction)eventActions.elementAt(i)).doAction(isPressed);
					}
					wasPressed = isPressed[0];
				} else {
					Thread.yield();
				}
			}
		}		
	}
}