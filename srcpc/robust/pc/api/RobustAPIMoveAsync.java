package robust.pc.api;

import robust.pc.api.RobustAPIMove.MoveInfo;

/**
 * RobustAPIMoveAsync provides information about moves that are done.  
 * @author Konrad Kulakowski
 */
public interface RobustAPIMoveAsync extends RobustAPI {
	interface MoveDoneListener {
		void handleMoveDoneEvent(MoveInfo type);
	}
	public void registerMoveDoneListener(MoveDoneListener moveListener);
}
