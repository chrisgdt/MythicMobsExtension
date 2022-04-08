package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "hotbarselected", author = "BerndiVader")
public class PlayersHotbarSelected extends AbstractCustomCondition implements IEntityCondition {
	RangedDouble slot;

	public PlayersHotbarSelected(String line, MythicLineConfig mlc) {
		super(line, mlc);
		slot = new RangedDouble(mlc.getString(new String[] { "slots", "slot" }, "0"));
	}

	@Override
	public boolean check(AbstractEntity entity) {
		if (entity.isPlayer()) {
			Player player = (Player) entity.getBukkitEntity();
			return slot.equals(player.getInventory().getHeldItemSlot());
		}
		return false;
	}
}
