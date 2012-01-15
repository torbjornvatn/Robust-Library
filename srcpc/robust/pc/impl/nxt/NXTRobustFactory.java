package robust.pc.impl.nxt;

import robust.pc.RobustFactory;
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

/**
 * @author Konrad Kulakowski
 */
public class NXTRobustFactory extends RobustFactory {

    private NXTRobustCommands commands = null;

    private NXTRobustCommands getRobustCommands() {
        if (commands == null) {
            RobustConfBean conf = RobustConfFactory.getConfig();
            commands = new NXTRobustCommands(conf.getHardwareAddress(), conf.getHardwareTransport());
        }
        return commands;
    }

    @Override
    public RobustAPISystem getRobustCommandsSystem() {
        return getRobustCommands();
    }

    @Override
    public RobustAPICompass getRobustCommandsCompass() {
        return getRobustCommands();
    }

    @Override
    public RobustAPIMove getRobustCommandsMove() {
        return getRobustCommands();
    }

    @Override
    public RobustAPITouch getRobustCommandsTouch() {
        return getRobustCommands();
    }

    @Override
    public RobustAPIVision getRobustCommandsVision() {
        return getRobustCommands();
    }

    @Override
    public RobustAPITouchAsync getRobustCommandsTouchAsync() {
        return getRobustCommands();
    }

    @Override
    public RobustAPIMoveAsync getRobustCommandsMoveAsync() {
        return getRobustCommands();
    }

    @Override
    public RobustAPISystemAsync getRobustCommandsSystemAsync() {
        return getRobustCommands();
    }

    @Override
    public RobustAPISonar getRobustCommandsSonar() {
        return getRobustCommands();
    }

    @Override
    public RobustAPISonarAsync getRobustCommandsSonarAsync() {
        return getRobustCommands();
    }

    @Override
    public RobustAPILight getRobustCommandsLight() {
        return getRobustCommands();
    }

    @Override
    public RobustAPILightAsync getRobustCommandsLightAsync() {
        return getRobustCommands();
    }

    @Override
    public boolean isAPISupported(Class<? extends RobustAPI> robustAPI) {
        if (RobustAPISystem.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPICompass.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPIMove.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPITouch.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPIVision.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPITouchAsync.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPIMoveAsync.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPISystemAsync.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPILight.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPILightAsync.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPISonar.class.isAssignableFrom(robustAPI)) {
            return true;
        } else if (RobustAPISonarAsync.class.isAssignableFrom(robustAPI)) {
            return true;
        } else {
            return false;
        }
    }
}