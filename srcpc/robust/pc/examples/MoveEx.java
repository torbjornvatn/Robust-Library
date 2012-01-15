package robust.pc.examples;

import java.io.IOException;

import org.apache.log4j.Logger;

import robust.pc.RobustFactory;
import robust.pc.api.RobustAPICompass;
import robust.pc.api.RobustAPIMove;
import robust.pc.api.RobustAPIMoveAsync;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPISystemAsync;
import robust.pc.api.RobustAPIMove.MoveInfo;

/**
 * @author Konrad Kulakowski
 */
public class MoveEx {

	public void passingObstacle(RobustAPIMove commands) {
		commands.moveForward(200, 100, 0);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(200, 70, 90);
		commands.moveCircly(-90, 100, 100);
		commands.moveForward(200, 70, 0);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(200, 70, 90);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(200, 70, 180);
	}

	public void square(RobustAPIMove commands) {
		commands.moveForward(300, 100, 0);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 70, 90);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 70, 180);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 70, 270);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 100, 0);
	}

	public void square2(RobustAPIMove commands) {
		commands.moveForward(200, 100, 0);
		commands.moveTurnInPlace(90, 100);
		commands.moveForward(200, 70, 90);
		commands.moveTurnInPlace(90, 100);
		commands.moveForward(200, 70, 180);
		commands.moveTurnInPlace(90, 100);
		commands.moveForward(200, 70, 270);
		commands.moveTurnInPlace(90, 100);
	}

	public void square3(RobustAPIMove commands) {
		commands.moveForward(300, 100, 0);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 70, 90);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 70, 180);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 70, 270);
		commands.moveCircly(90, 100, 100);
		commands.moveForward(300, 100, 0);
		commands.moveForward(200, 100, 0);
		commands.moveTurnInPlace(90, 100);
		commands.moveForward(200, 70, 90);
		commands.moveTurnInPlace(90, 100);
		commands.moveForward(200, 70, 180);
		commands.moveTurnInPlace(90, 100);
		commands.moveForward(200, 70, 270);
		commands.moveTurnInPlace(90, 100);
	}

	public void stopNowTest(RobustAPIMove commands) {
		commands.moveForward(3000, 100, 0);
		commands.moveCircly(90, 100, 100);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		commands.stopNow();
	}

	public void waitForMoveTest(final RobustAPIMove commands) {
		commands.moveForward(100, 100, 0);
		commands.moveTurnInPlace(180, 100);
		commands.moveTurnInPlace(-180, 100);
		commands.moveForward(100, 100, 0);
		commands.moveForward(100, 100, 0);
		commands.moveTurnInPlace(-180, 100);
		commands.moveForward(100, 100, 0);

		for (int i = 0; i < 5; i++) {
			MoveInfo moveInfo = commands.waitForMoveChange();
			System.out.println("Current move : " + moveInfo.name() + "("
					+ " speed: " + moveInfo.getSpeed() + " length: "
					+ moveInfo.getLength() + " radius: " + moveInfo.getRadius()
					+ " turn degs.: " + moveInfo.getTurnDegs() + ")");
		}
	}

	public void waitForMoveAndStopTest(final RobustAPIMove commands) {
		commands.moveForward(2000, 100, 0);

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				commands.stopNow();
				System.out.println("Stop now call!");
			}
		};

		t.start();

		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		
		MoveInfo moveInfo = commands.waitForMoveChange();
		System.out.println("Current move : " + moveInfo.name() + "("
				+ " speed: " + moveInfo.getSpeed() + " length: "
				+ moveInfo.getLength() + " radius: " + moveInfo.getRadius()
				+ " turn degs.: " + moveInfo.getTurnDegs() + ")");		
	}

	public void moveDoneHandlerTest(final RobustAPIMove commands,
			RobustAPIMoveAsync moveAsync) {
		moveAsync
				.registerMoveDoneListener(new RobustAPIMoveAsync.MoveDoneListener() {
					public void handleMoveDoneEvent(MoveInfo moveInfo) {
						System.out.println("Move in progress: "
								+ moveInfo.name() + "(" + " speed: "
								+ moveInfo.getSpeed() + " length: "
								+ moveInfo.getLength() + " radius: "
								+ moveInfo.getRadius() + " turn degs.: "
								+ moveInfo.getTurnDegs() + ")");
					}
				});

		commands.moveForward(100, 100, 0);
		commands.moveTurnInPlace(180, 100);
		commands.moveTurnInPlace(-180, 100);
		commands.moveForward(100, 100, 0);
		commands.moveForward(100, 100, 0);
		commands.moveTurnInPlace(-180, 100);
	}

	public void stopMoving(final RobustAPIMove commands,
			RobustAPIMoveAsync moveAsync) {
		commands.moveForward(100, 100, 0);
		commands.moveTurnInPlace(180, 100);
		commands.moveTurnInPlace(-180, 100);
		moveAsync
				.registerMoveDoneListener(new RobustAPIMoveAsync.MoveDoneListener() {
					public void handleMoveDoneEvent(MoveInfo moveNo) {
						commands.stopMoving();
					}
				});
	}

	public void moveStatusTest(final RobustAPIMove commands) {
		commands.moveForward(1500, 100, 0);
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			MoveInfo moveInfo = commands.moveStatus();

			System.out.println("Move " + moveInfo.name() + " done in "
					+ moveInfo.getPercentOfExecution() + "% " + "(speed: "
					+ moveInfo.getSpeed() + " length: " + moveInfo.getLength()
					+ " radius: " + moveInfo.getRadius() + " turn degs.: "
					+ moveInfo.getTurnDegs() + ")");
		}
	}

	public void runExample() {
		final RobustFactory factory = RobustFactory.getInstance();

		if (!factory.isAPISupported(RobustAPISystem.class)
				&& !factory.isAPISupported(RobustAPICompass.class)
				&& !factory.isAPISupported(RobustAPIMove.class)
				&& !factory.isAPISupported(RobustAPIMoveAsync.class)
				&& !factory.isAPISupported(RobustAPISystemAsync.class)) {
			throw new UnsupportedOperationException(
					"Not all indispensible APIs are supported");
		}

		final RobustAPISystem base = factory.getRobustCommandsSystem();
		final RobustAPICompass compas = factory.getRobustCommandsCompass();
		final RobustAPIMove move = factory.getRobustCommandsMove();
		final RobustAPIMoveAsync moveAsync = factory
				.getRobustCommandsMoveAsync();

		// init commands
		final RobustAPISystemAsync systemAsync = factory
				.getRobustCommandsSystemAsync();

		systemAsync
				.registerLogEvent(new RobustAPISystemAsync.BodyLoggerHandler() {
					public void handleLogEvent(String msg) {
						Logger.getLogger("Body").info("BODY: " + msg);
					}
				});

		base.startup();

		try {
			System.out.println("press ENTER to continue");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// compas.resetCompass();

		// passingObstacle(move);
		// stopMoving(move, moveAsync);
		// moveDoneHandlerTest(move, moveAsync);
		// waitForMoveTest(move);
		waitForMoveAndStopTest(move);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}		
		waitForMoveAndStopTest(move);
		// moveStatusTest(move);
		// stopNowTest(move);
		// try other: square square2 etc.

		try {
			System.out.println("press ENTER to finish");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		base.remoteShutdown();
		base.shutdown();
		System.exit(0);
	}

	public static void main(String[] args) {
		MoveEx ex = new MoveEx();
		ex.runExample();
	}
}