package robust.pc.api;

/**
 * @author Konrad Kulakowski
 */
public interface RobustAPISystem extends RobustAPI {
	/**
	 * Ends connections handling on server side
	 */
	public abstract void shutdown();
	
	/**
	 * Ends connection handling on hardware side
	 * Obviously it shoudl be called before shutdown
	 */
	public abstract void remoteShutdown();	

	/**
	 * Make the Robust commands running. Prior to call this method all
	 * permanent async method handlers have to be initialized 
	 */
	public abstract void startup();	

	/**
	 * ping method call 
	 * @return pong 
	 */
	public abstract String ping();
	
	/**
	 * @return body name
	 */
	public abstract String bodyName();
	
	/**
	 * Preferred format is major_version.minor_version
	 * @return body version
	 */
	public abstract String bodyVersion();
}