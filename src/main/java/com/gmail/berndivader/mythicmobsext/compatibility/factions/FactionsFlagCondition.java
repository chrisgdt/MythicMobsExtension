package com.gmail.berndivader.mythicmobsext.compatibility.factions;

import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;

public class FactionsFlagCondition extends AbstractCustomCondition implements ILocationCondition {
	String flagName;

	public FactionsFlagCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		flagName = mlc.getString(new String[] { "flagtype", "flag", "f" }, "monster").toLowerCase();
	}

	@Override
	public boolean check(AbstractLocation target) {
		return FactionsSupport.checkRegionFlag(BukkitAdapter.adapt(target), this.flagName);
	}
}
