package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.block.BlockFace;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "onsolidblock,insolidblock", author = "BerndiVader")
public class SolidBlockConditions extends AbstractCustomCondition implements ILocationCondition {
	private char c;

	public SolidBlockConditions(String line, MythicLineConfig mlc) {
		super(line, mlc);
		c = line.toUpperCase().charAt(0);
	}

	@Override
	public boolean check(AbstractLocation a) {
		switch (c) {
		case 'O':
			return BukkitAdapter.adapt(a).getBlock().getRelative(BlockFace.DOWN).getType().isSolid();
		case 'I':
			return BukkitAdapter.adapt(a).getBlock().getType().isSolid();
		}
		return false;
	}

}
