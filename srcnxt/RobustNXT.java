import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * @author Konrad Kulakowski <konrad (at) kulakowski.org>
 */
public class RobustNXT {
    private static DataInputStream dis = null;
    private static DataOutputStream dos = null;
    private static final Vector inQueue = new Vector(7);
    private static final Vector outQueue = new Vector(7);
    private static boolean isShutdown = false;
    private static BTReader btReaderThread = null;
    private static BTWriter btWriterThread = null;
    private static CommandProcessor cmdProcThread = null;
    private static CommandsFacade cmds = new CommandsFacade();
    private static ButtonListener shutdownHook = null;

    public static void sendBTMsg(NXTBTMsg msg) {
        synchronized (outQueue) {
            outQueue.insertElementAt(msg, 0);
            outQueue.notifyAll();
        }
    }

    // to support gracefully shutdown
    public static boolean isShutdown() {
        return isShutdown;
    }

    // //////////////////////
    // BT Communication threads
    public static class BTReader extends Thread {

        public void run() {
            while (!isShutdown) {
                NXTBTMsg msg = new NXTBTMsg();
                msg.fromStream(dis);
                synchronized (inQueue) {
                    inQueue.insertElementAt(msg, 0);
                    inQueue.notifyAll();
                }
            }
        }
    }

    public static class BTWriter extends Thread {

        public void run() {
            while (!isShutdown) {
                NXTBTMsg msg = null;
                synchronized (outQueue) {
                    if (outQueue.isEmpty()) {
                        try {
                            outQueue.wait();
                        } catch (InterruptedException e) {
                        }
                    } // if
                    msg = (NXTBTMsg) outQueue.elementAt(outQueue.size() - 1);
                    outQueue.removeElementAt(outQueue.size() - 1);
                }
                msg.toStream(dos);
            } // while
        } // run
    }

    // //////////////////////
    // Command processor
    public static class CommandProcessor extends Thread {
    	void handleBTMessage(NXTBTMsg msg) {
            // put your methods here
            switch (msg.methodType) {
                case NXTMessageType.PING_IN_MESSAGE_TYPE:
                    cmds.pingPongCmd(msg.methodUID);
                    break;
                case NXTMessageType.NXT_SHUTDOWN_IN_MESSAGE_TYPE:
                    cmds.shutdownCmd(shutdownHook);
                    break;
                case NXTMessageType.MSGTYPE_MOVE_FORWARD:
                    cmds.moveForward(msg);
                    break;
                case NXTMessageType.MSGTYPE_MOVE_TURN_IN_PLACE:
                    cmds.turnInPlace(msg);
                    break;
                case NXTMessageType.MSGTYPE_MOVE_TURN_CIRCLY:
                    cmds.turnCircly(msg);
                    break;
                case NXTMessageType.MSGTYPE_MOVE_STOP_NOW:
                	cmds.stopNow();
                	break;
                case NXTMessageType.MSGTYPE_MOVE_STOP:
                	cmds.stopMoving();
                	break;
                case NXTMessageType.MSGTYPE_MOVE_GET_STATUS:
                	cmds.moveStatus(msg.methodUID);
                	break;                	
                case NXTMessageType.MSGTYPE_GET_COMPASS:
                    cmds.getCompassDegs(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_CALIBRATE_COMPASS:
                    cmds.calibrateCompass(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_RESET_COMPASS:
                    cmds.resetCompass(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_TOUCH_SENSE_IS_PRESSED:
                    cmds.isTouchSensePressed(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_VISION_GET_SCENE:
                    cmds.getPrimitives(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_VISION_SET_FILTER_BY_COLOR:
                    cmds.setFilterByColor(msg);
                    break;
                case NXTMessageType.MSGTYPE_VISION_SET_FILTER_BY_KIND:
                    cmds.setFilterByKind(msg);
                    break;
                case NXTMessageType.MSGTYPE_VISION_SET_FILTER_BY_SIZE:
                    cmds.setFilterBySize(msg);
                    break;
                case NXTMessageType.MSGTYPE_SONAR_GET_DISTANCE:
                    cmds.getDistance(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_SONAR_GET_TARGET:
                    cmds.getSonarTarget(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_SONAR_SET_TARGET:
                    cmds.setSonarTarget(msg);
                    break;
                case NXTMessageType.MSGTYPE_SONAR_REGISTER_LISTENER:
                    cmds.registerProximityAlarm(msg);
                    break;
                case NXTMessageType.MSGTYPE_LIGHT_GET_VALUE:
                    cmds.getLightValue(msg.methodUID);
                    break;
                case NXTMessageType.MSGTYPE_LIGHT_REGISTER_LISTENER:
                    cmds.registerLightAlarm(msg);
                    break;
                default:
                    LoggerNXT.error("RobustNXT.CommandProcessor - unknown method");
            }    		
    	}	
    	
        public void run() {
            while (!isShutdown) {
                NXTBTMsg msg = null;
                synchronized (inQueue) {
                    if (inQueue.isEmpty()) {
                        try {
                            inQueue.wait();
                        } catch (InterruptedException e) {
                        }
                    }// if
                    msg = (NXTBTMsg) inQueue.elementAt(inQueue.size() - 1);
                    inQueue.removeElementAt(inQueue.size() - 1);
                }
                handleBTMessage(msg);
            }
        }
    }

    // //////////////////////
    // Mainly for exit
    public static class EnterButtonHandler implements ButtonListener {

        public void buttonPressed(Button b) {
            isShutdown = true;
            LoggerNXT.info("bye bye...");
            try {
                dis.close();
            } catch (Exception e) {
            }
            try {
                dos.close();
            } catch (Exception e) {
            }
            System.exit(0);
        }

        public void buttonReleased(Button b) {
        }
    }

    // /////////////////////
    // main
    public static void main(String[] args) {
        LCD.refresh();
               
        LoggerNXT.info("Welcome2RobUST");
        LoggerNXT.info("waiting 4 brain");

        // program exit
        shutdownHook = new EnterButtonHandler();
        Button.ENTER.addButtonListener(shutdownHook);

        BTConnection btc = Bluetooth.waitForConnection();
        
        LoggerNXT.info("brain is OK");
        dis = btc.openDataInputStream();
        dos = btc.openDataOutputStream();

        // initialize command processor class
        cmds.init();

        btReaderThread = new BTReader();
        btWriterThread = new BTWriter();
        cmdProcThread = new CommandProcessor();

        btWriterThread.start();
        cmdProcThread.start();
        btReaderThread.start();

        while (!isShutdown) {
            Thread.yield();
        }
        
    }
}