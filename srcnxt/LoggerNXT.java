/**
 * @author Konrad Kulakowski
 */
public class LoggerNXT {
	public static synchronized void trace(String msg) {
		System.out.print(msg + ";");
		System.out.flush();
	}
	public static synchronized void info(String msg) {
		System.out.println(msg);
		System.out.flush();
		NXTBTMsg btMsg = new NXTBTMsg(NXTMessageType.MSGTYPE_ASC_LOG_RESPONSE_TYPE,
				NXTMessageType.MSGTYPE_ASC_LOG_RESPONSE_TYPE, msg);
		RobustNXT.sendBTMsg(btMsg);
	}
	public static synchronized void error(String msg) {
		info("E" + msg);
	}		
	public static synchronized void debug(String msg) {
		info("D" + msg);
	}
}