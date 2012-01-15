package robust.pc.examples;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import robust.pc.RobustFactory;
import robust.pc.api.RobustAPISonarAsync;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPISystemAsync;
import robust.pc.util.InfoPanel;

/**
 * @author Konrad Kulakowski
 */
public class SonarSenseEx extends InfoPanel {
	private RobustAPISystem system = null;
	private RobustAPISystemAsync systemAsync = null;
	private RobustAPISonarAsync sonarAsync = null;

	private static int alarmDistance = 300; // 300 mm

	public SonarSenseEx() {
		super("Sonar sense test", "alarm distance ", "on change mode: ",
				"inform if closer than alarm  ",
				"inform if further than alarm ", "keep informing if closer ",
				"keep informing if further ");
		label12.setText(alarmDistance + "");
		SwingUtilities.updateComponentTreeUI(label12);
	}

	public void runExample() {
		final RobustFactory factory = RobustFactory
				.getInstance();
		system = factory.getRobustCommandsSystem();
		systemAsync = factory.getRobustCommandsSystemAsync();
		factory.getRobustCommandsSonar();
		sonarAsync = factory.getRobustCommandsSonarAsync();

		systemAsync
				.registerLogEvent(new RobustAPISystemAsync.BodyLoggerHandler() {
					
					public void handleLogEvent(String msg) {
						Logger.getLogger("Body").info("BODY: " + msg);
					}
				});

		system.startup();
		
		sonarAsync
				.registerProximityAlarmListener(new RobustAPISonarAsync.ProximityAlarmListner() {
					
					public int getAlarmDistance() {
						return alarmDistance;
					}

					
					public int getMode() {
						return 0; // inform on change
					}

					
					public void handleProximityAlarm(int distance) {
						label22.setText(distance + "");
						SwingUtilities.updateComponentTreeUI(label22);
					}
				});
		
		sonarAsync
		.registerProximityAlarmListener(new RobustAPISonarAsync.ProximityAlarmListner() {
			
			public int getAlarmDistance() {
				return alarmDistance;
			}

			
			public int getMode() {
				return 1; // inform on change
			}

			
			public void handleProximityAlarm(int distance) {
				label32.setText(distance + "");
				SwingUtilities.updateComponentTreeUI(label32);
			}
		});	
		
		sonarAsync
		.registerProximityAlarmListener(new RobustAPISonarAsync.ProximityAlarmListner() {
			
			public int getAlarmDistance() {
				return alarmDistance;
			}

			
			public int getMode() {
				return 2; // inform on change
			}

			
			public void handleProximityAlarm(int distance) {
				label42.setText(distance + "");
				SwingUtilities.updateComponentTreeUI(label42);
			}
		});		

		sonarAsync
		.registerProximityAlarmListener(new RobustAPISonarAsync.ProximityAlarmListner() {
			
			public int getAlarmDistance() {
				return alarmDistance;
			}

			
			public int getMode() {
				return 3; // inform on change
			}

			
			public void handleProximityAlarm(int distance) {
				label52.setText(distance + "");
				SwingUtilities.updateComponentTreeUI(label52);
			}
		});	
		
		sonarAsync
		.registerProximityAlarmListener(new RobustAPISonarAsync.ProximityAlarmListner() {
			public int getAlarmDistance() {
				return alarmDistance;
			}
			public int getMode() {
				return 4; // inform on change
			}
			public void handleProximityAlarm(int distance) {
				label62.setText(distance + "");
				SwingUtilities.updateComponentTreeUI(label62);
			}
		});
	}

	public static void main(String[] args) {
		SonarSenseEx example = new SonarSenseEx();
		example.runExample();
	}

	public void doActionOnClick() {
		system.remoteShutdown();
		system.shutdown();
		System.exit(0);
	}
}