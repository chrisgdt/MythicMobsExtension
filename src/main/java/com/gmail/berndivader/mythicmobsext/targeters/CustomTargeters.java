package com.gmail.berndivader.mythicmobsext.targeters;

import java.lang.reflect.InvocationTargetException;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.Externals;
import com.gmail.berndivader.mythicmobsext.externals.Internals;

public class CustomTargeters implements Listener {
	public CustomTargeters() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin());
	}

	@EventHandler
	public void onMythicTargetersLoad(MythicTargeterLoadEvent e) {
		SkillTargeter st;
		if ((st = getCustomTargeter(e.getTargeterName().toLowerCase(), e.getConfig(), e.getContainer().getManager())) != null) {
			Internals.tl++;
			e.register((ISkillTargeter) st);
		}
	}

	public static SkillTargeter getCustomTargeter(String s1, MythicLineConfig mlc, SkillExecutor executor) {
		SkillTargeter t = null;
		Class<? extends SkillTargeter> cl1 = null;
		if (Internals.targeters.containsKey(s1)) {
			cl1 = Main.getPlugin().internals.loader.loadT(s1);
		} else if (Externals.targeters.containsKey(s1)) {
			cl1 = Externals.targeters.get(s1);
		}
		if (cl1 != null) {
			try {
				t = cl1.getConstructor(SkillExecutor.class, MythicLineConfig.class).newInstance(executor, mlc);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		return t;
	}
}
