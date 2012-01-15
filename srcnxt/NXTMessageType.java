/**
 * NXTMessageType is shared between robustpc and robustnxt. Please keep the file identical 
 * in both modules
 * 
 * @author Konrad Kulakowski
 */
public class NXTMessageType {
	// base API
	public static final byte PING_IN_MESSAGE_TYPE = 0;
	public static final byte NXT_SHUTDOWN_IN_MESSAGE_TYPE = 2;
	public static final byte MSGTYPE_ASC_LOG_RESPONSE_TYPE = -1;
	
	// compass API
	public static final byte MSGTYPE_GET_COMPASS = 3;	
	public static final byte MSGTYPE_CALIBRATE_COMPASS = 4;
	public static final byte MSGTYPE_RESET_COMPASS = 5;

	// touch API
	public static final byte MSGTYPE_ASC_TOUCH_SENSE = -10;		
	public static final byte MSGTYPE_TOUCH_SENSE_IS_PRESSED = 11;
	public static final byte MSGTYPE_ASC_MOVE_DONE = -2;
	
	// sonar API
	public static final byte MSGTYPE_SONAR_GET_DISTANCE = 20;
	public static final byte MSGTYPE_SONAR_GET_TARGET = 21;
	public static final byte MSGTYPE_SONAR_SET_TARGET = 22;
	public static final byte MSGTYPE_SONAR_REGISTER_LISTENER = 23;	
	public static final byte MSGTYPE_ASC_SONAR_LISTENER = -24;
	
	// move API
	public static final byte MSGTYPE_MOVE_FORWARD = 100;
	public static final byte MSGTYPE_MOVE_TURN_IN_PLACE = 101;
	public static final byte MSGTYPE_MOVE_TURN_CIRCLY = 102;
	public static final byte MSGTYPE_MOVE_STOP = 103;
	public static final byte MSGTYPE_MOVE_STOP_NOW = 104;
	public static final byte MSGTYPE_MOVE_GET_STATUS = 105;
	public static final byte MSGTYPE_MOVE_WAIT_FOR_MOVE_CHANGE = 106;

	// vision API 
	public static final byte MSGTYPE_VISION_SET_FILTER_BY_SIZE = 120;
	public static final byte MSGTYPE_VISION_SET_FILTER_BY_COLOR = 121;
	public static final byte MSGTYPE_VISION_SET_FILTER_BY_KIND = 122;
	public static final byte MSGTYPE_VISION_GET_SCENE = 123;
        
        //light API
    public static final byte MSGTYPE_LIGHT_GET_VALUE = 30;
    public static final byte MSGTYPE_LIGHT_REGISTER_LISTENER = 31;
    public static final byte MSGTYPE_LIGHT_SET_FLOODLIGHT = 32;
    public static final byte MSGTYPE_ASC_LIGHT_LISTENER = 33;
}