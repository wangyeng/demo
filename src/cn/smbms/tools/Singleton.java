package cn.smbms.tools;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月21日 下午12:51:27
 * @ClassName 类名称
 * @Description 类描述 单例模式-静态内部类
 */
public class Singleton {

	private static Singleton singleton;

	private Singleton() {

	}

	public static class SingetinHelper {
		private static final Singleton INSTANCE = new Singleton();
	}

	public static Singleton getInstance() {
		singleton = SingetinHelper.INSTANCE;
		return singleton;
	}

	public static Singleton test() {
		return singleton;
	}
}
