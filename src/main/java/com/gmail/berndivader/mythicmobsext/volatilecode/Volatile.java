package com.gmail.berndivader.mythicmobsext.volatilecode;

import org.bukkit.Bukkit;

import com.gmail.berndivader.mythicmobsext.Main;

public class Volatile {
	public static Handler handler;

	static {
		String v, n;
		n = Bukkit.getServer().getClass().getPackage().getName();
		// TODO:
		// Change this to something that is more future proof
		// v = n.substring(n.lastIndexOf(46) + 1);
		v = "v1_21_R1";
		try {
			Class<?> c = Class.forName("com.gmail.berndivader.mythicmobsext.volatilecode." + v + ".Core");
			if (Handler.class.isAssignableFrom(c))
				handler = (Handler) c.getConstructor(new Class[0]).newInstance(new Object[0]);
		} catch (Exception ex) {
			if (ex instanceof ClassNotFoundException) {
				Main.logger.warning("Server version not supported!");
			}
			ex.printStackTrace();
		}
	}
}
