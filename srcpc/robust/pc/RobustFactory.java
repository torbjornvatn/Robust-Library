package robust.pc;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import robust.pc.api.RobustAPI;
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
import robust.pc.config.RobustConfBean;
import robust.pc.config.RobustConfFactory;

public abstract class RobustFactory {
	private static RobustFactory impl = null;	
	protected RobustFactory() {
	}
	
	@SuppressWarnings("unchecked")
	public static RobustFactory getInstance() {
		if (impl == null) {
			RobustConfBean conf = RobustConfFactory.getConfig();
			try {
				Class c = Class.forName(conf.getRobustFactory());
				impl = (RobustFactory) c.getConstructors()[0].newInstance(new Object[0]);
			} catch (IllegalArgumentException e) {
				Logger.getLogger(RobustFactory.class).error(e.getMessage(), e);
			} catch (SecurityException e) {
				Logger.getLogger(RobustFactory.class).error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				Logger.getLogger(RobustFactory.class).error(e.getMessage(), e);
			} catch (InstantiationException e) {
				Logger.getLogger(RobustFactory.class).error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Logger.getLogger(RobustFactory.class).error(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				Logger.getLogger(RobustFactory.class).error(e.getMessage(), e);
			}
		}
		return impl;
	}
	
	public abstract boolean isAPISupported(Class<? extends RobustAPI> robustAPI);

	public abstract RobustAPISystem getRobustCommandsSystem();
	public abstract RobustAPISystemAsync getRobustCommandsSystemAsync();
	public abstract RobustAPICompass getRobustCommandsCompass();
	public abstract RobustAPIVision getRobustCommandsVision();	
	public abstract RobustAPIMove getRobustCommandsMove();
	public abstract RobustAPIMoveAsync getRobustCommandsMoveAsync();
	public abstract RobustAPITouch getRobustCommandsTouch();
	public abstract RobustAPITouchAsync getRobustCommandsTouchAsync();
	public abstract RobustAPISonar getRobustCommandsSonar();
	public abstract RobustAPISonarAsync getRobustCommandsSonarAsync();
    public abstract RobustAPILight getRobustCommandsLight();
    public abstract RobustAPILightAsync getRobustCommandsLightAsync();
}