/**
 * @author Konrad Kulakowski
 */
public interface MoveGenerator {
	public abstract void addMove(NXTBTMsg msg);
	public abstract int getCurrentMove();
	public abstract void stopMoving();
	public abstract void stopNow();
	public abstract void removeLastMoves(int startId);
}