package robust.pc.impl.nxt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.apache.log4j.Logger;

import robust.pc.api.RobustAPICompass;
import robust.pc.api.RobustAPILight;
import robust.pc.api.RobustAPILightAsync;
import robust.pc.api.RobustAPIMove;
import robust.pc.api.RobustAPIMoveAsync;
import robust.pc.api.RobustAPISonar;
import robust.pc.api.RobustAPISonarAsync;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPISystemAsync;
import robust.pc.api.RobustAPITouch;
import robust.pc.api.RobustAPITouchAsync;
import robust.pc.api.RobustAPIVision;
import robust.pc.config.RobustConfFactory;
import robust.pc.util.Point2D;

import com.sun.jmx.snmp.daemon.CommunicationException;

import lejos.pc.comm.NXTCommBluecove;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * @author Konrad Kulakowski
 */
public class NXTRobustCommands extends NXTRobustCommandsBase implements
		RobustAPISystem, RobustAPISystemAsync, RobustAPITouch,
		RobustAPITouchAsync, RobustAPIMove, RobustAPIMoveAsync,
		RobustAPICompass, RobustAPIVision, RobustAPISonar, RobustAPISonarAsync,
		RobustAPILight, RobustAPILightAsync {

	// Administration API (Base API)
	public NXTRobustCommands(String btAddress, String protocol) {
		super(btAddress, protocol);
	}

	public void startup() throws CommunicationException {
		// Mac OS X hack!!
		// Apparently on my mac it is written in upper case, whilst bluecove
		// expect
		// lower case value
		System.setProperty("os.name", System.getProperty("os.name")
				.toLowerCase());
		String systemId = System.getProperty("os.name");

		// Hack for SoyLatte JVM (also on Mac)
		if ("darwin".equals(systemId)) {
			System.setProperty("os.name", "mac os x");
			systemId = "mac os x";
		}

		if (systemId.indexOf("mac os x") != -1) {
			nxtComm = new NXTCommBluecove();
		} else if (systemId.indexOf("linux") != -1) {
			nxtComm = new NXTCommBluecove();
		} else { // it is probably windows
			nxtComm = new NXTCommBluecove();
		}

		NXTInfo[] nxtInfo = new NXTInfo[1];
		nxtInfo[0] = new NXTInfo(NXTCommFactory.BLUETOOTH, "NXT", btAddress);
		Logger.getLogger(this.getClass()).info("Connecting to " + btAddress);

		boolean opened = false;
		try {
			opened = nxtComm.open(nxtInfo[0]);
		} catch (NXTCommException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		}

		if (!opened) {
			Logger.getLogger(this.getClass()).info(
					"Failed to open " + nxtInfo[0].name);
			throw new CommunicationException("Failed to open "
					+ nxtInfo[0].name);
		}

		InputStream is = nxtComm.getInputStream();

		OutputStream os = nxtComm.getOutputStream();

		dos = new DataOutputStream(os);
		dis = new DataInputStream(is);

		workersExecutorSrv.execute(new BTWriter());
		workersExecutorSrv.execute(new CommandProcessor());
		workersExecutorSrv.execute(new BTReader());
	}

	/**
	 * @see robust.pc.automatic.RobustCommands#shutdown()
	 */
	@SuppressWarnings("deprecation")
	public void shutdown() {
		isShutdown.set(true);
		try {
			dos.close();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		}
		try {
			dis.close();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		}
		try {
			nxtComm.close();
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		}
		asyncExecutorSrv.shutdownNow();
		workersExecutorSrv.shutdownNow();
	}

	/**
	 * @see robust.pc.automatic.RobustCommands#registerAsyncHandler(robust.pc.automatic.impl.nxt.NXTAsyncMethodHandler,
	 *      int)
	 */
	public void registerAsyncHandler(AsyncMethodHandler handler2) {
		NXTAsyncMethodHandler handler = (NXTAsyncMethodHandler) handler2;
		synchronized (asyncMethodsMap) {
			if (!asyncMethodsMap.containsKey(handler.getUID())) {
				asyncMethodsMap.put(handler.getUID(),
						new LinkedList<NXTAsyncMethodHandler>());
			}
			asyncMethodsMap.get(handler.getUID()).add(handler);
		}
	}

	/**
	 * Converts millimeters to internal unit (for NXT there is a servo degrees)
	 * 
	 * @param millimeters
	 * @return
	 */
	private int millis2internal(float millimeters) {
		final float scale200 = RobustConfFactory.getConfig().getUnitScale200();
		return (int) Math.round(scale200 / 200.0 * (float) millimeters);
	}

	private int internal2milis(float internals) {
		final float scale200 = RobustConfFactory.getConfig().getUnitScale200();
		return (int) Math.round(200.0 / scale200 * (float) internals);
	}

	public String bodyName() {
		return "scout";
	}

	public String bodyVersion() {
		return "0.1";
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see robust.pc.automatic.RobustCommands#ping()
	 */
	public String ping() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.PING_IN_MESSAGE_TYPE;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		try {
			resp = sync.take();
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
		return resp.getMsg();
	}

	public void remoteShutdown() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.NXT_SHUTDOWN_IN_MESSAGE_TYPE;
		sendBTMsg(req);
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void registerLogEvent(BodyLoggerHandler logListener) {
		class LogListenerWrapper extends NXTAsyncMethodHandler {

			private BodyLoggerHandler logListener = null;

			public void doAction(NXTBTMsg nxtMsg) {
				logListener.handleLogEvent(nxtMsg.getMsg());
			}

			public LogListenerWrapper(boolean isDisposable, int UID,
					BodyLoggerHandler logListener) {
				super(isDisposable, UID);
				this.logListener = logListener;
			}
		}

		registerAsyncHandler(new LogListenerWrapper(false,
				NXTMessageType.MSGTYPE_ASC_LOG_RESPONSE_TYPE, logListener));
	}

	// Move API
	public void moveForward(int length, int speed, int keepAzimuth) {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_MOVE_FORWARD;
		req.args = new int[4];

		// speed
		req.args[0] = millis2internal(speed);

		// epsilon - should be a function of speed
		req.args[1] = (int) Math.floor((float) req.args[0] / 50.0);

		// optional parameters:
		// length
		req.args[2] = millis2internal(length);

		req.args[3] = keepAzimuth;

		sendBTMsg(req);
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void moveCircly(int turnDegrees, int radius, int speed) {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_MOVE_TURN_CIRCLY;
		req.args = new int[5];

		// speed
		req.args[0] = millis2internal(speed);

		// epsilon - should be a function of speed
		req.args[1] = (int) Math.floor((float) req.args[0] / 40.0);

		// optional parameters:
		// turn degrees
		req.args[2] = turnDegrees;
		req.args[3] = millis2internal(radius);
		req.args[4] = millis2internal(RobustConfFactory.getConfig()
				.getDistanceBetweenWheels());

		sendBTMsg(req);
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void moveTurnInPlace(int turnDegrees, int speed) {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_MOVE_TURN_IN_PLACE;
		req.args = new int[4];

		// speed
		req.args[0] = millis2internal(speed);

		// epsilon - should be a function of speed
		req.args[1] = (int) Math.floor((float) req.args[0] / 40.0);

		// optional parameters:
		// turn degrees
		req.args[2] = turnDegrees;
		req.args[3] = millis2internal(RobustConfFactory.getConfig()
				.getDistanceBetweenWheels());

		sendBTMsg(req);
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void registerMoveDoneListener(MoveDoneListener moveListener) {
		class MoveDoneListenerWrapper extends NXTAsyncMethodHandler {

			private MoveDoneListener moveListener = null;

			public void doAction(NXTBTMsg nxtMsg) {

				MoveInfo mt = MoveInfo.getByCode(0);
				switch (nxtMsg.args[0]) {
				case NXTMessageType.MSGTYPE_MOVE_FORWARD:
					mt = MoveInfo.getByCode(1);
					break;
				case NXTMessageType.MSGTYPE_MOVE_TURN_CIRCLY:
					mt = MoveInfo.getByCode(2);
					break;
				case NXTMessageType.MSGTYPE_MOVE_TURN_IN_PLACE:
					mt = MoveInfo.getByCode(3);
					break;
				}
				mt = MoveInfo.init(mt, internal2milis(nxtMsg.args[1]),
						internal2milis(nxtMsg.args[2]), nxtMsg.args[3],
						nxtMsg.args[4], nxtMsg.args[5]);

				moveListener.handleMoveDoneEvent(mt);
			}

			public MoveDoneListenerWrapper(boolean isDisposable, int UID,
					MoveDoneListener moveListener) {
				super(isDisposable, UID);
				this.moveListener = moveListener;
			}
		}
		registerAsyncHandler(new MoveDoneListenerWrapper(false,
				NXTMessageType.MSGTYPE_ASC_MOVE_DONE, moveListener));
	}

	private void registerWaitForMoveHandler(int methodFakeUID) {
		class MoveDoneListenerWrapper extends NXTAsyncMethodHandler {
			private int methodFakeUID;

			public void doAction(NXTBTMsg nxtMsg) {
				try {
					syncMethodsMap.get(methodFakeUID).put(nxtMsg);
				} catch (InterruptedException e) {
				}
			}

			public MoveDoneListenerWrapper(boolean isDisposable, int UID,
					int methodFakeUID) {
				super(isDisposable, UID);
				this.methodFakeUID = methodFakeUID;
			}
		}
		registerAsyncHandler(new MoveDoneListenerWrapper(true,
				NXTMessageType.MSGTYPE_ASC_MOVE_DONE, methodFakeUID));
	}

	// Compass API
	public int getCompassDegs() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_GET_COMPASS;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		try {
			resp = sync.take();
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
		return resp.args[0];
	}

	public void calibrateCompass() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_CALIBRATE_COMPASS;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		try {
			sync.take();
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
	}

	public void resetCompass() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_RESET_COMPASS;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		try {
			sync.take();
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
	}

	// Touch API
	public int[] getTouchSenseState() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_TOUCH_SENSE_IS_PRESSED;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		try {
			resp = sync.take();
			return resp.args;
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
		return null;
	}

	public void registerTouchListener(TouchListener touchListener) {
		class TouchListenerWrapper extends NXTAsyncMethodHandler {

			private TouchListener touchListener = null;

			public TouchListenerWrapper(boolean isDisposable, int UID,
					TouchListener touchListener) {
				super(isDisposable, UID);
				this.touchListener = touchListener;
			}

			public void doAction(NXTBTMsg nxtMsg) {
				touchListener.handleTouchEvent(nxtMsg.args);
			}
		}
		registerAsyncHandler(new TouchListenerWrapper(false,
				NXTMessageType.MSGTYPE_ASC_TOUCH_SENSE, touchListener));
	}

	// Vision API
	public void filterPrimitivesByColor(int colorNo) {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_VISION_SET_FILTER_BY_COLOR;
		req.args = new int[1];
		req.args[0] = colorNo;
		sendBTMsg(req);
		// it just waits to sending primitives out of queue
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void filterPrimitivesBySize(int objectSize) {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_VISION_SET_FILTER_BY_SIZE;
		req.args = new int[1];
		req.args[0] = objectSize;
		sendBTMsg(req);
		// it just waits to sending primitives out of queue
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void filterPrimitivestByKind(int objectKind) {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_VISION_SET_FILTER_BY_SIZE;
		req.args = new int[1];
		req.args[0] = objectKind;
		sendBTMsg(req);
		// it just waits to sending primitives out of queue
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public List<Primitive2D> getScene2D() {
		class Primitive2DImpl implements Primitive2D {

			private int kind;
			private Point2D<Integer> position;
			private int size;
			private int color;

			public Primitive2DImpl(int kind, int posX, int posY, int size,
					int color) {
				this.kind = kind;
				this.position = new Point2D<Integer>(posX, posY);
				this.size = size;
				this.color = color;
			}

			public int getKind() {
				return kind;
			}

			public Point2D getPosition() {
				return position;
			}

			public int getSizeOfPrimitive() {
				return size;
			}

			public int getColorOfPrimitive() {
				return color;
			}
		}

		final NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_VISION_GET_SCENE;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		try {
			resp = sync.take();
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage(), e);
			return null;
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}

		// number of primitives
		final int sceneSize = resp.args == null ? 0 : resp.args.length / 5;

		final List<Primitive2D> scene = new ArrayList<Primitive2D>();
		for (int i = 0; i < sceneSize; i++) {
			scene.add(new Primitive2DImpl(resp.args[5 * i],
					resp.args[5 * i + 1], resp.args[5 * i + 2],
					resp.args[5 * i + 3], resp.args[5 * i + 4]));
		}

		return scene;
	}

	// Sonar API
	public int getDistance() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_SONAR_GET_DISTANCE;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		final float sonarScale = RobustConfFactory.getConfig()
				.getUsonicUnitScale();
		try {
			resp = sync.take();
			return (int) sonarScale * resp.args[0];
		} catch (InterruptedException e) {
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
		return -1;
	}

	public int getSonarTarget() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_SONAR_GET_TARGET;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		try {
			resp = sync.take();
			return resp.args[0];
		} catch (InterruptedException e) {
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
		return -1;
	}

	public void setSonarTarget(int degs) {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_SONAR_SET_TARGET;
		req.args = new int[1];
		req.args[0] = degs;
		sendBTMsg(req);
		// it just waits to sending primitives out of queue
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void registerProximityAlarmListener(ProximityAlarmListner listener) {
		final float sonarScale = RobustConfFactory.getConfig()
				.getUsonicUnitScale();

		class ProximityListenerWrapper extends NXTAsyncMethodHandler {

			private ProximityAlarmListner proximityListener = null;

			public ProximityListenerWrapper(boolean isDisposable, int methodID,
					ProximityAlarmListner proximityListener) {
				super(isDisposable, methodID);
				this.proximityListener = proximityListener;
			}

			public void doAction(NXTBTMsg nxtMsg) {
				proximityListener.handleProximityAlarm((int) sonarScale
						* nxtMsg.args[0]);
			}
		}

		int methodUID = asynchMethodUID.decrementAndGet();

		// register data handling hook
		registerAsyncHandler(new ProximityListenerWrapper(false, methodUID,
				listener));

		// register listener remotely
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodUID;
		req.methodType = NXTMessageType.MSGTYPE_SONAR_REGISTER_LISTENER;
		req.args = new int[2];
		req.args[0] = listener.getMode();
		req.args[1] = (int) ((float) listener.getAlarmDistance() / sonarScale);
		sendBTMsg(req);

		// wait until remote registration request go out the message queue
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void registerLightListener(LightListener listener) {

		class LightListenerWrapper extends NXTAsyncMethodHandler {

			private LightListener lightListener = null;

			public LightListenerWrapper(boolean isDisposable, int methodID,
					LightListener lightListener) {
				super(isDisposable, methodID);
				this.lightListener = lightListener;
			}

			public void doAction(NXTBTMsg nxtMsg) {
				lightListener.handleLightValue(nxtMsg.args[0]);
			}
		}

		int methodUID = asynchMethodUID.decrementAndGet();

		// register data handling hook
		registerAsyncHandler(new LightListenerWrapper(false, methodUID,
				listener));

		// register listener remotely
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodUID;
		req.methodType = NXTMessageType.MSGTYPE_LIGHT_REGISTER_LISTENER;
		req.args = new int[2];
		req.args[0] = listener.getAlarmValue(); // alarmValue
		req.args[1] = listener.getTolerance(); // tolerance
		System.out.println("send register lightList " + req.methodType);
		sendBTMsg(req);

		// wait until remote registration request go out the message queue
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public int getLightValue() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_LIGHT_GET_VALUE;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		try {
			resp = sync.take();
			return resp.args[0];
		} catch (InterruptedException e) {
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
		return -1;
	}

	public void stopNow() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_MOVE_STOP_NOW;
		sendBTMsg(req);
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public void stopMoving() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_MOVE_STOP;
		sendBTMsg(req);
		while (!isSent(req)) {
			Thread.yield();
		}
	}

	public MoveInfo moveStatus() {
		NXTBTMsg req = new NXTBTMsg();
		req.methodUID = methodsUID.incrementAndGet();
		req.methodType = NXTMessageType.MSGTYPE_MOVE_GET_STATUS;
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(req.methodUID, sync);
		sendBTMsg(req);
		// get result
		NXTBTMsg resp = null;
		try {
			resp = sync.take();
			
			MoveInfo mt = MoveInfo.getByCode(0);
			switch (resp.args[0]) {
			case NXTMessageType.MSGTYPE_MOVE_FORWARD:
				mt = MoveInfo.getByCode(1);
				break;
			case NXTMessageType.MSGTYPE_MOVE_TURN_CIRCLY:
				mt = MoveInfo.getByCode(2);
				break;
			case NXTMessageType.MSGTYPE_MOVE_TURN_IN_PLACE:
				mt = MoveInfo.getByCode(3);
				break;
			}
			mt = MoveInfo.init(mt, internal2milis(resp.args[1]),
					internal2milis(resp.args[2]), resp.args[3], resp.args[4], resp.args[5]);			
			return mt;
			
		} catch (InterruptedException e) {
		} finally {
			syncMethodsMap.remove(req.methodUID);
		}
		return null;
	}

	public MoveInfo waitForMoveChange() {
		int methodFakeUID = methodsUID.incrementAndGet();
		ArrayBlockingQueue<NXTBTMsg> sync = new ArrayBlockingQueue<NXTBTMsg>(1);
		syncMethodsMap.put(methodFakeUID, sync);
		registerWaitForMoveHandler(methodFakeUID);

		NXTBTMsg resp;

		try {
			resp = sync.take();

			MoveInfo mt = MoveInfo.getByCode(0);
			switch (resp.args[0]) {
			case NXTMessageType.MSGTYPE_MOVE_FORWARD:
				mt = MoveInfo.getByCode(1);
				break;
			case NXTMessageType.MSGTYPE_MOVE_TURN_CIRCLY:
				mt = MoveInfo.getByCode(2);
				break;
			case NXTMessageType.MSGTYPE_MOVE_TURN_IN_PLACE:
				mt = MoveInfo.getByCode(3);
				break;
			}
			mt = MoveInfo.init(mt, internal2milis(resp.args[1]),
					internal2milis(resp.args[2]), resp.args[3], resp.args[4], resp.args[5]);
			return mt;
		} catch (InterruptedException e) {
		} finally {
			syncMethodsMap.remove(methodFakeUID);
		}
		return null;
	};
};