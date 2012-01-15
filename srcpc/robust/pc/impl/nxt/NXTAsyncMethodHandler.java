package robust.pc.impl.nxt;

/**
 * @author Konrad Kulakowski
 */
public abstract class NXTAsyncMethodHandler implements Runnable, AsyncMethodHandler {
	private boolean isDisposable;
	private NXTBTMsg msg = null;
	private int UID;
	
	public NXTAsyncMethodHandler(boolean isDisposable, int UID) {
		this.isDisposable = isDisposable;
		this.UID = UID;
	}

	/* (non-Javadoc)
	 * @see robust.pc.automatic.impl.nxt.AsyncMethodHandler#isDisposable()
	 */
	public boolean isDisposable() {
		return isDisposable;
	}
	
	public int getUID() {
		return UID;
	}
	
	public void setMsg(NXTBTMsg msg) {
		this.msg = msg;
	}

	/* (non-Javadoc)
	 * @see robust.pc.automatic.impl.nxt.AsyncMethodHandler#doAction(robust.pc.automatic.impl.nxt.BTMsg)
	 */
	public abstract void doAction(NXTBTMsg msg);

	public void run() {
		doAction(msg);
	}	
}
