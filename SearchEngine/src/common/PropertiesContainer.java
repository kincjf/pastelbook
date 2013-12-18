package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesContainer {
	public static Properties prop = new Properties();
	String path;
	/**
	 * @param args
	 */
	public PropertiesContainer(String path) {
		if(prop != null) {
			prop = new Properties();
			this.path = path;
		}
	}
	
	public PropertiesContainer() {
		if(prop != null) {
			path = "config.properties";
			prop = new Properties();
		}
		// 기본
	}
	
	
	public void loadFile() {
		try {
			prop.load(new FileInputStream(path));
		} catch (IOException e) {
			System.out.println("SetProperties - loadFile err");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void loadClasspathFile() {
		try {
			//System.out.println("loadClassPathFile " + PropertiesContainer.class.getPath());
			prop.load(PropertiesContainer.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			System.out.println("SetProperties - loadClasspathFile err");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
