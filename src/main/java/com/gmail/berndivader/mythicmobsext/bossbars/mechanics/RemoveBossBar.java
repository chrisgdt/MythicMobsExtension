package com.gmail.berndivader.mythicmobsext.bossbars.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.bossbars.BossBars;

public class RemoveBossBar extends SkillMechanic implements ITargetedEntitySkill {

	PlaceholderString title;

	public RemoveBossBar(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		title = mlc.getPlaceholderString("title", "Bar");
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		if (abstract_entity.isPlayer()) {
			if (BossBars.contains(abstract_entity.getUniqueId())) {
				BossBars.removeBar((Player) abstract_entity.getBukkitEntity(), title.get(data, abstract_entity));
				return SkillResult.SUCCESS;
			}
		}
		return SkillResult.CONDITION_FAILED;
	}

}
