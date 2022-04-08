package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "forceslot", author = "BerndiVader")
public class ForceSlot extends SkillMechanic implements ITargetedEntitySkill {
	int slot;

	public ForceSlot(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.slot = mlc.getInteger("slot", 0);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_target) {
		if (abstract_target.isPlayer()) {
			Player player = (Player) abstract_target.getBukkitEntity();
			player.getInventory().setHeldItemSlot(slot);
		}
		return SkillResult.SUCCESS;
	}

}
