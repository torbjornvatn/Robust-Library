package robust.pc.examples;

import org.apache.log4j.Logger;

import robust.pc.RobustFactory;
import robust.pc.api.RobustAPICompass;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPISystemAsync;

/**
 * @author Konrad Kulakowski
 */
public class CompassCalibrationEx {
	public void runExample() {
		final RobustFactory factory = RobustFactory.getInstance();
		final RobustAPISystem commands = factory.getRobustCommandsSystem();
		final RobustAPISystemAsync systemAsync = factory.getRobustCommandsSystemAsync();
		
		systemAsync.registerLogEvent(new RobustAPISystemAsync.BodyLoggerHandler() {
			public void handleLogEvent(String msg) {
				Logger.getLogger("Body").info("BODY: " + msg);
			}			
		});
		
		commands.startup();
		
		final RobustAPICompass compass = factory.getRobustCommandsCompass();
		compass.calibrateCompass();		
		
		commands.remoteShutdown();
		commands.shutdown();
		System.exit(0);		
	}
	
	public static void main(String[] args) {
		CompassCalibrationEx ex = new CompassCalibrationEx();
		ex.runExample();
	}
}