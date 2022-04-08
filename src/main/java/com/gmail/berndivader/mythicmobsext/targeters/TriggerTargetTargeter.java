package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "triggerstarget", author = "BerndiVader")
public class TriggerTargetTargeter extends ISelectorEntity {
	public TriggerTargetTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity> targets = new HashSet<>();
		AbstractEntity target;
		if (data.getTrigger().isPlayer()) {
			if ((target = BukkitAdapter
					.adapt(Utils.getTargetedEntity((Player) data.getTrigger().getBukkitEntity(), length))) != null) {
				targets.add(target);
			}
			;
		} else if ((target = data.getTrigger().getTarget()) != null) {
			targets.add(target);
		}
		return this.applyOffsets(targets);
	}
}
