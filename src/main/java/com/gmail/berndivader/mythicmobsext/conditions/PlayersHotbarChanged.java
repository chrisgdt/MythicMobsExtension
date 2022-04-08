package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "hotbar,hotbarchanged", author = "BerndiVader")
public class PlayersHotbarChanged extends AbstractCustomCondition implements IEntityCondition {
	RangedDouble millis;

	public PlayersHotbarChanged(String line, MythicLineConfig mlc) {
		super(line, mlc);
		millis = new RangedDouble(mlc.getString(new String[] { "milliseconds", "millis", "ms", "m" }, "<1"));
	}

	@Override
	public boolean check(AbstractEntity entity) {
		if (entity.isPlayer() && entity.getBukkitEntity().hasMetadata(Utils.meta_SLOTCHANGEDSTAMP)) {
			Player player = (Player) entity.getBukkitEntity();
			double time = System.currentTimeMillis()
					- player.getMetadata(Utils.meta_SLOTCHANGEDSTAMP).get(0).asDouble();
			return millis.equals(time);
		}
		return false;
	}
}
