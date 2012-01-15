import java.util.Vector;

import lejos.nxt.Motor;

/**
 * @author Konrad Kulakowski <konrad (at) kulakowski.org>
 */
public class MoveGeneratorB extends Thread implements MoveGenerator {
	private final Vector movesQueue = new Vector(15);
	private int movesCounter = 0;
	private boolean isStopped = false;

	private Motor leftMotor = null;
	private Motor rightMotor = null;

	private byte currentMethodId = 0;
	// in tacho degrees for selected
	private int currentMoveLength = 0;
	private int previousMoveLength = 0;
	// motor speed for the chosen motor
	private int currentMoveSpeed = 0; // from -900 to 900
	private int currentRadius = 0;
	private int currentTurnDegs = 0;

	private int percentOfExecution = 0;

	public MoveGeneratorB(Motor leftMotor, Motor rightMotor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see MoveGenerator#addMove(NXTBTMsg)
	 */
	public void addMove(NXTBTMsg msg) {
		synchronized (movesQueue) {
			isStopped = false;
			movesQueue.insertElementAt(msg, 0);
			movesQueue.notifyAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see MoveGenerator#getCurrentMove()
	 */
	public int getCurrentMove() {
		return movesCounter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see MoveGenerator#stopMoving()
	 */
	public void stopMoving() {
		synchronized (movesQueue) {
			movesQueue.clear();
		}
		LoggerNXT.info("stop moving");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see MoveGenerator#stopNow()
	 */
	public void stopNow() {
		synchronized (movesQueue) {
			movesQueue.clear();
			isStopped = true;
			leftMotor.stop();
			rightMotor.stop();
		}
		LoggerNXT.info("stop now");
	}

	public void notifyMoveChange() {

		int[] actionParams = new int[6];
		actionParams[0] = currentMethodId;
		actionParams[1] = currentMoveSpeed;
		actionParams[2] = currentMoveLength;
		actionParams[3] = currentRadius;
		actionParams[4] = currentTurnDegs;
		actionParams[5] = percentOfExecution;

		NXTBTMsg btMsg = new NXTBTMsg(NXTMessageType.MSGTYPE_ASC_MOVE_DONE,
				NXTMessageType.MSGTYPE_ASC_MOVE_DONE, actionParams);

		RobustNXT.sendBTMsg(btMsg);
	}

	public void moveStatus(int methodUID) {

		int[] actionParams = new int[6];
		actionParams[0] = currentMethodId;
		actionParams[1] = currentMoveSpeed;
		actionParams[2] = currentMoveLength;
		actionParams[3] = currentRadius;
		actionParams[4] = currentTurnDegs;
		actionParams[5] = percentOfExecution;

		NXTBTMsg btMsg = new NXTBTMsg(methodUID, 
				NXTMessageType.MSGTYPE_MOVE_GET_STATUS,
				actionParams);

		RobustNXT.sendBTMsg(btMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see MoveGenerator#removeLastMoves(int)
	 */
	public void removeLastMoves(int startId) {
		synchronized (movesQueue) {
			for (int i = 0; i < startId - movesCounter; i++) {
				if (movesQueue.elementAt(0) != null) {
					movesQueue.removeElementAt(0);
				}
			}
		}
	}

	private int moveForward(int currentMoveSpeed, int currentMoveLength) {
		if (currentMoveSpeed > 0) {		
			rightMotor.setSpeed(currentMoveSpeed);
			leftMotor.setSpeed(currentMoveSpeed);
			rightMotor.regulateSpeed(false);
			leftMotor.regulateSpeed(false);
			rightMotor.forward();
			leftMotor.forward();
			rightMotor.regulateSpeed(true);
			leftMotor.regulateSpeed(true);
		} else {
			rightMotor.setSpeed((-1)*currentMoveSpeed);
			leftMotor.setSpeed((-1)*currentMoveSpeed);
			rightMotor.regulateSpeed(false);
			leftMotor.regulateSpeed(false);
			rightMotor.backward();
			leftMotor.backward();
			rightMotor.regulateSpeed(true);
			leftMotor.regulateSpeed(true);			
		}
		return currentMoveLength;
	}

	private int moveCircly(float externalWheelMoveSpeed, float degs,
			float internalRadius, float distanceBetweenWheels) {

		float externalRadius = internalRadius + distanceBetweenWheels;

		float internalArcLength = 2 * (float) Math.PI * internalRadius
				* (float) Math.abs(degs) / (float) 360.0;
		float externalArcLength = 2 * (float) Math.PI * externalRadius
				* (float) Math.abs(degs) / (float) 360.0;

		float internalWheelMoveSpeed;  
		
		if (externalWheelMoveSpeed > 0) {
			internalWheelMoveSpeed = externalWheelMoveSpeed
				* (internalArcLength / externalArcLength);
		} else {
			internalWheelMoveSpeed = externalWheelMoveSpeed
			* (internalArcLength / externalArcLength) * (-1);			
		}

		// right motor is A, left motor is B
		// degs > 0, turn right, i.e. left motor has to run faster
		if (degs > 0) {
			rightMotor.setSpeed((int) Math.round(internalWheelMoveSpeed));
			leftMotor.setSpeed((int) Math.round(externalWheelMoveSpeed));
		} else if (degs < 0) {
			rightMotor.setSpeed((int) Math.round(externalWheelMoveSpeed));
			leftMotor.setSpeed((int) Math.round(internalWheelMoveSpeed));
		} else {
			rightMotor.setSpeed((int) Math.round(externalWheelMoveSpeed));
			leftMotor.setSpeed((int) Math.round(externalWheelMoveSpeed));
		}
		
		if (externalWheelMoveSpeed > 0) {
			rightMotor.regulateSpeed(false);
			leftMotor.regulateSpeed(false);
			rightMotor.forward();
			leftMotor.forward();
			rightMotor.regulateSpeed(true);
			leftMotor.regulateSpeed(true);
		} else {
			rightMotor.regulateSpeed(false);
			leftMotor.regulateSpeed(false);
			rightMotor.backward();
			leftMotor.backward();
			rightMotor.regulateSpeed(true);
			leftMotor.regulateSpeed(true);			
		}

		// for procedure controlling wait to move
		return (int) externalArcLength;
	}

	private int moveInPlace(float moveSpeed, float degs,
			float distanceBetweenWheels) {

		float moveDistance = 2 * (float) Math.PI
				* ((float) 0.5 * distanceBetweenWheels)
				* (float) Math.abs(degs) / (float) 360.0;

		rightMotor.regulateSpeed(false);
		leftMotor.regulateSpeed(false);
		rightMotor.setSpeed((int) Math.round(moveSpeed));
		leftMotor.setSpeed((int) Math.round(moveSpeed));
		if (degs > 0) {
			rightMotor.backward();
			leftMotor.forward();
		} else if (degs < 0) {
			rightMotor.forward();
			leftMotor.backward();
		} else {
			rightMotor.forward();
			leftMotor.forward();
		}
		rightMotor.regulateSpeed(true);
		leftMotor.regulateSpeed(true);

		return (int) moveDistance;
	}

	private void waitToMove(int epsilon, int correction, int currentMoveLength,
			Motor motorOfMeasure) {
		int delta = 0;
		while (!isStopped) { // single move loop watch
			percentOfExecution = (100 * Math.abs(motorOfMeasure.getTachoCount()))
					/ currentMoveLength;
			delta = currentMoveLength - Math.abs(motorOfMeasure.getTachoCount());
			if (delta >= epsilon - correction) {
				Thread.yield();
			} else {
				break;
			}
		}
	}

	public void run() {
		NXTBTMsg msg = null;

		// time to move end

		int epsilon = 1;
		Motor observerMotor = rightMotor;
		int correction = 0;
		int moveId = 0;

		rightMotor.resetTachoCount();
		leftMotor.resetTachoCount();
		// Regulator must be disabled!
		// rightMotor.regulateSpeed(false);
		// leftMotor.regulateSpeed(false);

		while (true) { // moves loop
			synchronized (movesQueue) {
				if (movesQueue.isEmpty() || isStopped) {
					if (rightMotor.isMoving())
						rightMotor.stop();
					if (leftMotor.isMoving())
						leftMotor.stop();
					if (previousMoveLength != 0) {
						correction = 0;
						LoggerNXT.info("move " + (movesCounter - 1)
								+ " finished with correction " + correction
								+ " speed " + observerMotor.getSpeed());
						currentMethodId = 0;
						currentMoveLength = 0;
						currentMoveSpeed = 0;
						currentRadius = 0;
						currentTurnDegs = 0;
						percentOfExecution = 0;
						notifyMoveChange();
					}
					previousMoveLength = 0;
					// Regulator must be disabled
					try {
						movesQueue.wait();
					} catch (InterruptedException e) {
						continue;
					}
				}
				msg = (NXTBTMsg) movesQueue.elementAt(movesQueue.size() - 1);
				movesQueue.removeElementAt(movesQueue.size() - 1);
			} // synchronized

			currentMethodId = msg.methodType;
			currentMoveSpeed = msg.args[0];
			currentMoveLength = 0;
			currentRadius = 0;
			currentTurnDegs = 0;

			epsilon = msg.args[1];

			if (previousMoveLength != 0) {
				correction = previousMoveLength - Math.abs(observerMotor.getTachoCount());
				LoggerNXT.info("move " + (movesCounter - 1)
						+ " finished with correction " + correction + " speed "
						+ observerMotor.getSpeed());
			}

			rightMotor.resetTachoCount();
			leftMotor.resetTachoCount();
			moveId++;

			switch (currentMethodId) {
			case NXTMessageType.MSGTYPE_MOVE_FORWARD:
				currentMoveLength = moveForward(currentMoveSpeed, msg.args[2]);
				observerMotor = rightMotor;
				break; // break from switch
			case NXTMessageType.MSGTYPE_MOVE_TURN_CIRCLY:
				currentRadius = msg.args[2];
				currentTurnDegs = msg.args[3];
				currentMoveLength = moveCircly(currentMoveSpeed, msg.args[2],
						msg.args[3], msg.args[4]);
				observerMotor = msg.args[2] >= 0 ? leftMotor : rightMotor;
				break;
			case NXTMessageType.MSGTYPE_MOVE_TURN_IN_PLACE:
				currentTurnDegs = msg.args[2];
				currentMoveLength = moveInPlace(currentMoveSpeed, msg.args[2],
						msg.args[3]);
				observerMotor = msg.args[2] >= 0 ? leftMotor : rightMotor;
			}

			notifyMoveChange();

			previousMoveLength = currentMoveLength;
			waitToMove(epsilon, correction, currentMoveLength, observerMotor);

			movesCounter++;
		}
	}
}