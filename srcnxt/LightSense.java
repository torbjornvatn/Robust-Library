import java.util.Vector;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

/**
 * @author Konrad Kulakowski
 */
public class LightSense {

    private LightSensor light;
    private Vector eventActions = new Vector();

    public LightSense(SensorPort port, boolean flood) {
        light = new LightSensor(port);
        light.setFloodlight(flood);
    //LightObserver
    }

    public void init() {
        LightObserver observer = new LightObserver();
        observer.start();
        LoggerNXT.info("light init");
    }

    public void addEventAction(LightEventAction action) {
        eventActions.insertElementAt(action, eventActions.size());
    }

    public void getValue(int methodUID) {
        RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
                NXTMessageType.MSGTYPE_LIGHT_GET_VALUE, new int[]{light.readValue()}));
    }

    public void registerLightListener(NXTBTMsg msg) {
        LoggerNXT.info("reg["+msg.args[0]+","+msg.args[1]+"]");
        eventActions.insertElementAt(new LightEventAction(msg.methodUID, msg.args[0], msg.args[1]), eventActions.size());
    }

    public class LightEventAction implements EventAction {

        private int methodUID;
        private int alarmValue;
        private int tolerance;
        private boolean active = true;

        public LightEventAction(int methodUID, int alarmValue, int tolerance) {
            this.methodUID = methodUID;
            this.alarmValue = alarmValue;
            this.tolerance = tolerance;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void doAction(int[] actionParams) {
            if (isActive()) {
                NXTBTMsg btMsg = new NXTBTMsg(methodUID,
                        NXTMessageType.MSGTYPE_ASC_LIGHT_LISTENER, actionParams);
                RobustNXT.sendBTMsg(btMsg);
            }
        }

        public int getAlarmValue() {
            return alarmValue;
        }

        public void setAlarmValue(int alarmValue) {
            this.alarmValue = alarmValue;
        }

        public int getTolerance() {
            return tolerance;
        }

        public void setTolerance(int tolerance) {
            this.tolerance = tolerance;
        }
    }

    class LightObserver extends Thread {

        int[] buffer = new int[1];

        public void run() {
            int prevValue = -1;
            while (true) {
                int value = light.readValue();               
                if (value != prevValue) {
                    for (int i = 0; i < eventActions.size(); i++) {
                        final LightEventAction action = (LightEventAction) eventActions.elementAt(i);
                        buffer[0] = value;
                        int max = action.getAlarmValue() + action.getTolerance();
                        int min = action.getAlarmValue() - action.getTolerance();
                        if (value >= min && value <= max) {
                            action.doAction(buffer);
                        }
                    }
                }
                prevValue = value;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
}
