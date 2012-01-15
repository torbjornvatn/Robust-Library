package robust.pc.impl.nxt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lejos.pc.comm.NXTComm;

import org.apache.log4j.Logger;

/**
 * @author Konrad Kulakowski
 */
public class NXTRobustCommandsBase {
	protected String btAddress;
	protected NXTComm nxtComm = null;
	protected DataOutputStream dos = null;
	protected DataInputStream dis = null;
	protected AtomicInteger methodsUID = new AtomicInteger(0);

	// the range from 0 to 1000 is reserved for special purposes;
	protected AtomicInteger asynchMethodUID = new AtomicInteger(-1001);
	protected AtomicBoolean isShutdown = new AtomicBoolean(false);

	// communications queues
	protected Queue<NXTBTMsg> inQueue = new LinkedList<NXTBTMsg>();
	protected Queue<NXTBTMsg> outQueue = new LinkedList<NXTBTMsg>();

	// methods call synchronization layer
	protected Map<Integer, ArrayBlockingQueue<NXTBTMsg>> syncMethodsMap = new HashMap<Integer, ArrayBlockingQueue<NXTBTMsg>>();
	protected Map<Integer, LinkedList<NXTAsyncMethodHandler>> asyncMethodsMap = new HashMap<Integer, LinkedList<NXTAsyncMethodHandler>>();

	protected ExecutorService asyncExecutorSrv = Executors.newCachedThreadPool();
	protected ExecutorService workersExecutorSrv = Executors
			.newFixedThreadPool(3);

	// ////////////////////////
	//
	public NXTRobustCommandsBase(String btAddress, String protocol) {
		this.btAddress = btAddress;
		if (!protocol.equals("BT")) {
			throw new UnsupportedOperationException("Transport " + protocol
					+ " is unsupported");
		}
	}

	protected void sendBTMsg(NXTBTMsg btMsg) {
		synchronized (outQueue) {
			outQueue.add(btMsg);
			outQueue.notifyAll();
		}
	}

	protected boolean isSent(NXTBTMsg msg) {
		return !outQueue.contains(msg);
	}

	// //////////////////////
	// BT Communication threads
	public class BTReader implements Runnable {
		public void run() {
			Thread.currentThread().setName("BTReader");
			while (!isShutdown.get()) {
				NXTBTMsg msg = new NXTBTMsg();
				msg.fromStream(dis);
				 Logger.getLogger(this.getClass()).info("(in) id:" +
				 msg.methodUID + ", type: " + msg.methodType);
				synchronized (inQueue) {
					inQueue.add(msg);
					inQueue.notifyAll();
				}
			}
		}
	}

	public class BTWriter implements Runnable {
		public void run() {
			Thread.currentThread().setName("BTWriter");
			try {
				while (!isShutdown.get()) {
					NXTBTMsg msg = null;
					synchronized (outQueue) {
						if (outQueue.isEmpty()) {
							try {
								outQueue.wait();
							} catch (InterruptedException e) {
							}
						} // if
						msg = (NXTBTMsg) outQueue.remove();
					}
					 Logger.getLogger(this.getClass()).info("(out) id:" +
					 msg.methodUID + ", type: " + msg.methodType);
					msg.toStream(dos);
				} // while
			} catch (RuntimeException e) {
				// Shutdown exceptions
			}
		} // run
	}

	public class CommandProcessor implements Runnable {
		public void run() {
			Thread.currentThread().setName("CommandProcessor");
			try {
				while (!isShutdown.get()) {
					NXTBTMsg msg = null;
					synchronized (inQueue) {
						if (inQueue.isEmpty()) {
							try {
								inQueue.wait();
							} catch (InterruptedException e) {
							}
						}// if
						msg = (NXTBTMsg) inQueue.remove();

					}
					// this is synchronous method call
					if (msg.methodUID >= 0) {
						if (!syncMethodsMap.containsKey(msg.methodUID)) {
							Logger.getLogger(this.getClass()).error(
									"Method with UID: " + msg.methodUID
											+ "not found");
							continue;
						}
						if (!syncMethodsMap.get(msg.methodUID).offer(msg)) {
							Logger.getLogger(this.getClass()).error(
									"Strange since there should "
											+ "be always place for response "
											+ "in the synchronization queue");
						}
					} else {
						synchronized (asyncMethodsMap) {
							if (!asyncMethodsMap.containsKey(msg.methodUID)) {
								Logger.getLogger(this.getClass()).error(
										"Method with UID: " + msg.methodUID
												+ "not found");
								continue;
							}
							List<NXTAsyncMethodHandler> hList = asyncMethodsMap
									.get(msg.methodUID);
							Object[] hArray = hList.toArray();
							for (Object h : hArray) {
								NXTAsyncMethodHandler handler = (NXTAsyncMethodHandler) h;
								if (handler.isDisposable()) {
									hList.remove(handler);
									if (hList.size() == 0) {
										asyncMethodsMap.remove(msg.methodUID);
									}
								}
								handler.setMsg(msg);
								asyncExecutorSrv.execute(handler);
							} // for
						} // synchronized
					} // else
				} // while
			} catch (RuntimeException e) {
				// Shutdown exception
			}
		} // run
	} // CommandProcessor
}
