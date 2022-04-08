package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.Collection;
import java.util.HashSet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "targetstarget", author = "BerndiVader")
public class TargetsTargetTargeter extends ISelectorEntity {
	public TargetsTargetTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity> targets = new HashSet<>();
		Collection<AbstractEntity> tt = data.getEntityTargets();
		for (AbstractEntity target : tt) {
			if (target != null) {
				if (target.isPlayer()) {
					AbstractEntity pt = BukkitAdapter
							.adapt(Utils.getTargetedEntity((Player) target.getBukkitEntity(), length));
					if (pt != null)
						targets.add(pt);
				} else if (target.getTarget() != null) {
					targets.add(target.getTarget());
				}
			}
		}
		return this.applyOffsets(targets);
	}
}
