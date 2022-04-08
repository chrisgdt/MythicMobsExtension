package com.gmail.berndivader.mythicmobsext.compatibility.mobarena;

import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;

public class InMobArenaCondition extends AbstractCustomCondition implements ILocationCondition {
	public InMobArenaCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractLocation location) {
		return MobArenaSupport.mobarena.inRegion(BukkitAdapter.adapt(location));
	}
}
