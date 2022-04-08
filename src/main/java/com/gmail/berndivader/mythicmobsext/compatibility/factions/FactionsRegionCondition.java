package com.gmail.berndivader.mythicmobsext.compatibility.factions;

import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;

public class FactionsRegionCondition extends AbstractCustomCondition implements ILocationCondition {
	String[] regions;
	int size;

	public FactionsRegionCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		regions = mlc.getString(new String[] { "regions", "region", "factions", "faction", "r", "f" }, "").toLowerCase()
				.split(",");
		size = regions.length;
	}

	@Override
	public boolean check(AbstractLocation target) {
		return FactionsSupport.inFaction(BukkitAdapter.adapt(target), regions);
	}
}