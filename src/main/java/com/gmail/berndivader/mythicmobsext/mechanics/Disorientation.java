package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "disorientation", author = "BerndiVader")
public class Disorientation extends SkillMechanic implements ITargetedEntitySkill {

	byte state = 0;

	public Disorientation(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		state |= mlc.getBoolean("look", true) ? 0b1 : 0b0;
		state |= mlc.getBoolean("position", true) ? 0b10 : 0b00;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		if (abstract_entity.isPlayer()) {
			Player player = (Player) abstract_entity.getBukkitEntity();
			if (player.hasMetadata(Utils.meta_DISORIENTATION)) {
				player.removeMetadata(Utils.meta_DISORIENTATION, Main.getPlugin());
			} else {
				player.setMetadata(Utils.meta_DISORIENTATION, new FixedMetadataValue(Main.getPlugin(), state));
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}