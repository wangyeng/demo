package cn.smbms.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月21日 下午12:15:47
 * @ClassName 类名称
 * @Description 类描述 读取配置文件的工具类-单例模式
 */
public class ConfigManager {

	private static ConfigManager configManager = new ConfigManager();
	private static Properties properties;

	// 私有构造器-读取数据库配置文件
	private ConfigManager() {
		String configFile = "database.properties";
		properties = new Properties();
		InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream(configFile);
		try {
			properties.load(is);
			is.close();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 全局访问点-(懒汉模式)
//	public static synchronized ConfigManager getInstances() {
//		if (configManager == null) {
//			configManager = new ConfigManager();
//		}
//		return configManager;
//	}

	// 饿汉模式
	public static ConfigManager getInstance() {
		return configManager;
	}

	public String getValue(String key) {
		return properties.getProperty(key);
	}

}
