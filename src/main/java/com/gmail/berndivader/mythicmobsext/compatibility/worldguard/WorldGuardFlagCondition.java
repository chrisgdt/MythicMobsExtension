package com.gmail.berndivader.mythicmobsext.compatibility.worldguard;

import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;

public class WorldGuardFlagCondition extends AbstractCustomCondition implements ILocationCondition {

	String flag_name;
	String args;

	public WorldGuardFlagCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);

		flag_name = mlc.getString("flag", "mob-spawning");
		args = mlc.getString("args", new String()).toLowerCase();
	}

	@Override
	public boolean check(AbstractLocation location) {
		return WorldGuardUtils.checkFlagAtLocation(BukkitAdapter.adapt(location), flag_name, args);
	}

}
