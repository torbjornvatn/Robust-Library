package robust.pc.examples;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;


import robust.pc.RobustFactory;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPISystemAsync;
import robust.pc.api.RobustAPITouch;
import robust.pc.api.RobustAPITouchAsync;
import robust.pc.util.InfoPanel;

/**
 * @author Konrad Kulakowski
 */
public class TouchSenseEx extends InfoPanel {
	final static RobustFactory factory = RobustFactory.getInstance();
	final static RobustAPISystem base = factory.getRobustCommandsSystem();	
	final static RobustAPITouch touch = factory.getRobustCommandsTouch();
	final static RobustAPITouchAsync touchAsync = factory.getRobustCommandsTouchAsync();
	
	
	public void doActionOnClick() {
		base.remoteShutdown();
		base.shutdown();
		System.exit(0);
	}
	
	public void runExample() {
		// init commands
		final RobustAPISystemAsync systemAsync = factory.getRobustCommandsSystemAsync();
		
		systemAsync.registerLogEvent(new RobustAPISystemAsync.BodyLoggerHandler() {
			
			public void handleLogEvent(String msg) {
				Logger.getLogger("Body").info("BODY: " + msg);
			}			
		});
		
		touchAsync.registerTouchListener(new RobustAPITouchAsync.TouchListener() {
			
			public void handleTouchEvent(int[] touchStates) {
				label12.setText("change: " + (touchStates[0] == 0 ? "released" : "pressed"));
				SwingUtilities.updateComponentTreeUI(label12);
			}			
		});
				
		base.startup();
				
		while (true) {
			if (touch.getTouchSenseState()[0] == 0) {
				label22.setText(" is released");
			} else {
				label22.setText(" is pressed");				
			}
			SwingUtilities.updateComponentTreeUI(label22);
			try {
				for (int i = 1; i <= 5; i++) {
					label32.setText((5-i)+"");
					SwingUtilities.updateComponentTreeUI(label32);
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
			}
		}
	}
	
	public TouchSenseEx() {
		super("touch sense test", "async sensor reading: ", "sync method call: ", "sync method call timer: ", "", "", "");
	}

	public static void main(String[] args) {
		TouchSenseEx ex = new TouchSenseEx();
		ex.runExample();
	}	
}