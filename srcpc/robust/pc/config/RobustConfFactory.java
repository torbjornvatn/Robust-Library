package robust.pc.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Konrad Kulakowski
 */
public class RobustConfFactory {
	private static RobustConfBean config = null;
	public static RobustConfBean getConfig() {
		if (config != null) {
			return config;
		}
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(RobustConfBean.class);
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(RobustConfBean.CONFIG_NAME);
			if (is == null) {
				is = new FileInputStream(RobustConfBean.CONFIG_NAME);
			} 
			if (is == null) {
				throw new IOException("configuration file " + RobustConfBean.CONFIG_NAME + " not found");
			}
			config = (RobustConfBean) xstream.fromXML(is);
			is.close();
			return config;
		} catch (IOException e) {
			throw (RuntimeException) new RuntimeException().initCause(e);
		}
	}
}