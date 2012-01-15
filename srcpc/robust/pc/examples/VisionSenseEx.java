package robust.pc.examples;

import java.util.List;

import org.apache.log4j.Logger;

import robust.pc.RobustFactory;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPISystemAsync;
import robust.pc.api.RobustAPIVision;
import robust.pc.util.InfoPanel;

/**
 * @author Konrad Kulakowski
 */
public class VisionSenseEx extends InfoPanel {
	private RobustAPISystem base = null;
	private RobustAPIVision vision = null;

	public VisionSenseEx() {
		super("Vision sense test", "Target status: ", "Moving towards: ",
				"Target numbers: ", "Position: ", "Size: ", "Color: ");
	}

	public void runExample() {
		RobustFactory factory = RobustFactory.getInstance();
		base = factory.getRobustCommandsSystem();
		vision = factory.getRobustCommandsVision();

		final RobustAPISystemAsync systemAsync = factory.getRobustCommandsSystemAsync();
		
		systemAsync.registerLogEvent(new RobustAPISystemAsync.BodyLoggerHandler() {
			
			public void handleLogEvent(String msg) {
				Logger.getLogger("Body").info("BODY: " + msg);
			}			
		});

		base.startup();

		RobustAPIVision.Primitive2D previousPrimitive = null;

		for (int i = 0;; i++) {
			List<RobustAPIVision.Primitive2D> primitives = vision.getScene2D();

			// it assumes that the biggest primitive is the rectangle
			// that we are looking for
			if (primitives.size() == 0) {
				label12.setText("target not found");
				label22.setText("");
				label32.setText("");
				label42.setText("");
				label52.setText("");
				label62.setText("");
			} else {
				label12.setText("target found");

				RobustAPIVision.Primitive2D currentPrimitive = primitives
						.get(0);

				if (previousPrimitive != null) {
					if (previousPrimitive.getPosition().getX() > currentPrimitive
							.getPosition().getX()) {
						label22.setText(", moving left");
					} else {
						label22.setText(", moving right");
					}
				}

				previousPrimitive = currentPrimitive;

				label32.setText(primitives.size() + "");
				label42.setText("(" + currentPrimitive.getPosition().getX()
						+ ", " + currentPrimitive.getPosition().getY() + ")");
				label52.setText(currentPrimitive.getSizeOfPrimitive() + "");
				label62.setText(currentPrimitive.getColorOfPrimitive() + "");
			}
			updateData();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void main(String[] args) {
		VisionSenseEx ex = new VisionSenseEx();
		ex.runExample();
	}
	
	public void doActionOnClick() {
		base.remoteShutdown();
		base.shutdown();
		System.exit(0);
	}
}