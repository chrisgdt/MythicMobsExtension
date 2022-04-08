package com.gmail.berndivader.mythicmobsext.mechanics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.Externals;
import com.gmail.berndivader.mythicmobsext.externals.Internals;

public class CustomMechanics implements Listener {
	Internals internals = Main.internals;

	public CustomMechanics() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin());
	}

	@EventHandler
	public void onMMSkillLoad(MythicMechanicLoadEvent e) {
		String mech = e.getMechanicName().toLowerCase();

		if (Internals.mechanics.containsKey(mech)) {
			if (registerMechanic(internals.loader.loadM(mech), e)) {
				Internals.ml++;
			}
		} else if (Externals.mechanics.containsKey(mech)) {
			if (registerMechanic(Externals.mechanics.get(mech), e)) {
				Externals.ml++;
			}
		}
	}

	private boolean registerMechanic(Class<? extends SkillMechanic> cl1, MythicMechanicLoadEvent e) {
		SkillMechanic skill = null;
		try {
			Constructor<? extends SkillMechanic> construct = cl1.getConstructor(SkillExecutor.class, String.class, MythicLineConfig.class);
			// TODO : InvocationTargetException when loading castif
			// comes from MythicMobs, the field this.line of SkillMechanic is always null so e.getContainer().getConfigLine() is null and CastIf gets an exception

			//skill = construct.newInstance(e.getContainer().getManager(), e.getContainer().getConfigLine(), e.getConfig());
			skill = construct.newInstance(e.getContainer().getManager(), e.getConfig().getLine(), e.getConfig());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		if (skill != null) {
			e.register(skill);
			return true;
		}
		return false;
	}
}
