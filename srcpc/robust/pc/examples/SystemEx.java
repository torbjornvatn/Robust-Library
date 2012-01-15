package robust.pc.examples;

import org.apache.log4j.Logger;

import robust.pc.RobustFactory;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPISystemAsync;

/**
 * @author Konrad Kulakowski
 */
public class SystemEx {
	public static void main(String[] args) {
		SystemEx ex = new SystemEx();
		ex.runExample();
	}
	
	public void runExample() {
		final RobustFactory factory = RobustFactory.getInstance();
		final RobustAPISystem system = factory.getRobustCommandsSystem();
		final RobustAPISystemAsync systemAsync = factory.getRobustCommandsSystemAsync();
		
		systemAsync.registerLogEvent(new RobustAPISystemAsync.BodyLoggerHandler() {			
			public void handleLogEvent(String msg) {
				Logger.getLogger("Body").info("BODY: " + msg);
			}			
		});
		
		system.startup();
		
		System.out.println("brick said: " + system.ping());
		
		system.remoteShutdown();
		system.shutdown();		
	}
}