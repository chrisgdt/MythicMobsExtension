package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.berndivader.mythicmobsext.externals.*;
@ExternalAnnotation(name = "lastdamager", author = "BerndiVader")
public class LastDamagerTargeter extends ISelectorEntity {

	public LastDamagerTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity> targets = new HashSet<>();
		EntityDamageEvent e = data.getCaster().getEntity().getBukkitEntity().getLastDamageCause();
		if (e instanceof EntityDamageByEntityEvent) {
			targets.add(BukkitAdapter.adapt(((EntityDamageByEntityEvent) e).getDamager()));
		}
		return this.applyOffsets(targets);
	}

}
