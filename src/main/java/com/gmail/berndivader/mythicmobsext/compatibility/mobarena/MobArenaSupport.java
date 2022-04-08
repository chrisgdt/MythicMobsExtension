package com.gmail.berndivader.mythicmobsext.compatibility.mobarena;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.garbagemule.MobArena.MobArenaHandler;
import com.gmail.berndivader.mythicmobsext.Main;

public class MobArenaSupport implements Listener {
	static String pluginName;
	public static MobArenaHandler mobarena;

	static {
		pluginName = "MobArena";
		mobarena = new MobArenaHandler();
	}

	public MobArenaSupport() {
		Main.pluginmanager.registerEvents(this, Main.getPlugin());
		Main.logger.info("using " + pluginName);
	}

	@EventHandler
	public void onMythicMobsConditionsLoadEvent(MythicConditionLoadEvent e) {
		String conditionName = e.getConditionName().toLowerCase();
		switch (conditionName) {
		case "inmobarena":
		case "inmobarena_ext": {
			e.register(new InMobArenaCondition(e.getConfig().getLine(), e.getConfig()));
			break;
		}
		}
	}
}