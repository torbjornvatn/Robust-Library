package robust.pc.impl.nxt;

/**
 * @author Konrad Kulakowski
 */
public interface AsyncMethodHandler {
	/**
	 * Indicates wheteher after call these handler should be removed from
	 * handlers map. If true it will be removed.
	 * @return
	 */
	public abstract boolean isDisposable();

	/**
	 * It is called when the response addressed to this handler comes; 
	 * Response given as a Msg depends on implementation; 
	 * @param msg
	 */
	public abstract void doAction(NXTBTMsg msg);

}