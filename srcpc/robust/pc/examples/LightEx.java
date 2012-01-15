package robust.pc.examples;

import robust.pc.RobustFactory;
import robust.pc.api.RobustAPILight;
import robust.pc.api.RobustAPILightAsync;
import robust.pc.api.RobustAPISystem;
import robust.pc.api.RobustAPILightAsync.LightListener;

/**
 * @author Jacek Rzucidlo
 */
public class LightEx {

    RobustFactory factory = null;
    RobustAPISystem base = null;
    RobustAPILight light = null;
    RobustAPILightAsync async = null;

    public LightEx() {
        factory = RobustFactory.getInstance();
        base = factory.getRobustCommandsSystem();
        light = factory.getRobustCommandsLight();
        async = factory.getRobustCommandsLightAsync();
        base.startup();

    }

    public void registerLightListener(LightListener listener) {
        async.registerLightListener(listener);
    }

    public int getLightValue() {
        return light.getLightValue();
    }

    public static void main(String[] args) {
        LightEx test = new LightEx();
        System.out.println(test.getLightValue());
    }
}
