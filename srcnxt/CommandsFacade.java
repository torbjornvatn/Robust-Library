import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.I2CSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;	

/**
 * @author Konrad Kulakowski
 */
public class CommandsFacade {
    private TouchSense touch = null;
    private CompassSense compass = null;
    private MoveGeneratorB moveGenerator = null;
    private VisionSense visionSubsystem = null;
    private SonarSense sonar = null;
    private LightSense light = null;

    // System
    public void init() {
        touch = new TouchSense(SensorPort.S4);
        sonar = new SonarSense(SensorPort.S3, Motor.C);
        light = new LightSense(SensorPort.S2,true);

        moveGenerator = new MoveGeneratorB(Motor.B, Motor.A);        
        moveGenerator.start();

        // compass = new CompassSense(SensorPort.S1);
        // visionSubsystem = new VisionSense(SensorPort.S2);
        
        // sonar.init();
        // light.init();
    }

    public void pingPongCmd(int methodUID) {
        LoggerNXT.info("ping()");
        RobustNXT.sendBTMsg(new NXTBTMsg(methodUID, (byte) 0, "pong"));
    }

    public void shutdownCmd(ButtonListener shutdownHook) {
        moveGenerator.stopMoving();
        shutdownHook.buttonPressed(Button.ENTER);
    }

    // Compas
    public void getCompassDegs(int methodUID) {
        RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
                NXTMessageType.MSGTYPE_GET_COMPASS, (int) compass.getDegreesCartesian()));
    }

    public void calibrateCompass(int methodUID) {
        compass.calibrateCompass(Motor.B, Motor.A);
        RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
                NXTMessageType.MSGTYPE_CALIBRATE_COMPASS, 0));
    }

    public void resetCompass(int methodUID) {
        compass.resetCartesianZero();
        RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
                NXTMessageType.MSGTYPE_RESET_COMPASS, 0));
    }

    // Move
    public void moveForward(NXTBTMsg msg) {
        moveGenerator.addMove(msg);
    }

    public void turnInPlace(NXTBTMsg msg) {
        moveGenerator.addMove(msg);
    }

    public void turnCircly(NXTBTMsg msg) {
        moveGenerator.addMove(msg);
    }

    // Touch
    public void isTouchSensePressed(int methodUID) {
        RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
                NXTMessageType.MSGTYPE_TOUCH_SENSE_IS_PRESSED,
                new int[]{touch.isPressed() ? 1 : 0}));
    }

    // Vision
    public void setFilterByKind(NXTBTMsg msg) {
        visionSubsystem.setFilterByKind(msg);
    }

    public void setFilterByColor(NXTBTMsg msg) {
        visionSubsystem.setFilterByColor(msg);
    }

    public void setFilterBySize(NXTBTMsg msg) {
        visionSubsystem.setFilterBySize(msg);
    }

    public void getPrimitives(int methodUID) {
        visionSubsystem.getPrimitives(methodUID);
    }

    // Sonar
    public void setSonarTarget(NXTBTMsg msg) {
        sonar.setSonarTarget(msg);
    }

    public void getSonarTarget(int methodUID) {
        sonar.getSonarTarget(methodUID);
    }

    public void getDistance(int methodUID) {
        sonar.getDistance(methodUID);
    }

    public void registerProximityAlarm(NXTBTMsg msg) {
        sonar.registerProximityAlarmListener(msg);
    }

    public void registerLightAlarm(NXTBTMsg msg) {
        LoggerNXT.info("cmdregll");
        light.registerLightListener(msg);
    }
    
    public void getLightValue(int methodUID) {
        light.getValue(methodUID);
    }
    
    public void stopNow() {
    	moveGenerator.stopNow();
    }
    
    public void stopMoving() {
    	moveGenerator.stopMoving();
    }
    
    public void moveStatus(int methodUID) {
    	moveGenerator.moveStatus(methodUID);
    }
}