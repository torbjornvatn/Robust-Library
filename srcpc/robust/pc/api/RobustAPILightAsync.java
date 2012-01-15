package robust.pc.api;

/**
 * @author Konrad Kulakowski
 */
public interface RobustAPILightAsync {

	public interface LightListener {
		void handleLightValue(int value);
        int getAlarmValue();
        int getTolerance();
	}

	void registerLightListener(LightListener listener);
}
