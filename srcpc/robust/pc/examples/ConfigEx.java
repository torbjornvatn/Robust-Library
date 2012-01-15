package robust.pc.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import robust.pc.config.RobustConfBean;
import robust.pc.impl.nxt.NXTRobustFactory;

/**
 * @author Konrad Kulakowski
 */
public class ConfigEx {
	
	public void setField(RobustConfBean config, String fieldName, String value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field f = RobustConfBean.class.getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(config, value);		
	}
	public void setField(RobustConfBean config, String fieldName, int value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field f = RobustConfBean.class.getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(config, value);		
	}	
	public void setField(RobustConfBean config, String fieldName, float value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field f = RobustConfBean.class.getDeclaredField(fieldName);
		f.setAccessible(true);
		f.set(config, value);		
	}		
	
	public void setDefault(RobustConfBean config) {
		try {
			setField(config, "hardwareArchitecture", "NXT");
			setField(config, "hardwareAddress", "00:16:53:03:04:E5");
			setField(config, "hardwareTransport", "BT");
			setField(config, "distanceBetweenWheels", 126);
			setField(config, "unitScale200", 410);
			setField(config, "robustFactory", NXTRobustFactory.class.getName());
			setField(config, "usonicUnitScale", (float)9.52);
		} catch (Exception e) {
			Logger.getLogger(ConfigEx.class).error(e.getMessage(), e);
		} 
	}	
	
	public void generateSampleConfigFile() {
		XStream xstream = new XStream(new DomDriver());
		RobustConfBean config = new RobustConfBean();
		xstream.processAnnotations(RobustConfBean.class);

		setDefault(config);
		
		File f = new File(".");
		System.out.println(f.getAbsolutePath());
		
		File file = new File("srcpc/robust/pc/config/" + RobustConfBean.CONFIG_NAME);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			xstream.toXML(config, fos);
			System.out.print(xstream.toXML(config));
			fos.close();
		} catch (FileNotFoundException e) {
			Logger.getLogger(ConfigEx.class).error(e.getMessage(), e);
		} catch (IOException e) {
			Logger.getLogger(ConfigEx.class).error(e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		new ConfigEx().generateSampleConfigFile();
	}
}
