import java.awt.Rectangle;
import lejos.nxt.addon.NXTCam;
import lejos.nxt.SensorPort;

/**
 * @author Konrad Kulakowski
 */
public class VisionSense extends NXTCam {
	private boolean areRectanglesAllowed = true;
	private int minSize = -1;
	private int colorId = -1;

	public VisionSense(SensorPort port) {
		super(port);
		sendCommand('A'); // sort objects by size
		// the biggest go first
		sendCommand('E'); // start tracking
		
		if (!"NXTCAM".equals(getSensorType().substring(0, 6))) {
			LoggerNXT.error("NXTCam on port S" + (port.getId()+1) + " not found");
		}
	}

	public void setFilterByKind(NXTBTMsg msg) {
		int kind = msg.args[0];
		areRectanglesAllowed = (kind == 1); // rectangles are 0
	}

	public void setFilterBySize(NXTBTMsg msg) {
		this.minSize = msg.args[0];
	}

	public void setFilterByColor(NXTBTMsg msg) {
		this.colorId = msg.args[0];
	}

	public void getPrimitives(int methodUID) {
		if (!areRectanglesAllowed) {
			// send back empty list
			RobustNXT.sendBTMsg(new NXTBTMsg(methodUID,
					NXTMessageType.MSGTYPE_VISION_GET_SCENE, new int[0]));
		}

		int objectsOnScene = getNumberOfObjects();
		// max no of objects is set to 8 or less (magic number)
		objectsOnScene = (8 < objectsOnScene) ? 8 : objectsOnScene;

		int[] objectBuffer = new int[5 * objectsOnScene];
		int actualPrimitivesNo = 0;
		
		for (int i = 0; i < objectsOnScene; i++) {
			Rectangle r = getRectangle(i);
			int objectColor = getObjectColor(i) + 1;

			if (minSize > 0 && minSize > r.width * r.height) {
				continue;
			}
			if (colorId > -1 && colorId != objectColor) {
				continue;
			}
			objectBuffer[5 * i] = 1; // rectangle
			objectBuffer[5 * i + 1] = r.x;
			objectBuffer[5 * i + 2] = r.y;
			objectBuffer[5 * i + 3] = r.width*r.height;
			objectBuffer[5 * i + 4] = objectColor;
			actualPrimitivesNo++;
		}

		int[] args = new int[5 * actualPrimitivesNo];
				
		for (int j = 0; j < actualPrimitivesNo; j++) {
			args[5 * j] = objectBuffer[5 * j];
			args[5 * j + 1] = objectBuffer[5 * j + 1];
			args[5 * j + 2] = objectBuffer[5 * j + 2];
			args[5 * j + 3] = objectBuffer[5 * j + 3];
			args[5 * j + 4] = objectBuffer[5 * j + 4];
		}

		NXTBTMsg msg = new NXTBTMsg(methodUID,
				NXTMessageType.MSGTYPE_VISION_GET_SCENE, args);

		RobustNXT.sendBTMsg(msg);
	}
}