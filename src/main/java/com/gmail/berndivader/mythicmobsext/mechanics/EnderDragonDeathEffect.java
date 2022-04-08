package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "endereffect", author = "BerndiVader")
public class EnderDragonDeathEffect extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

	public EnderDragonDeathEffect(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
		this.playEnderEffect(BukkitAdapter.adapt(target));
		return SkillResult.SUCCESS;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		this.playEnderEffect(target.getBukkitEntity().getLocation());
		return SkillResult.SUCCESS;
	}

	private void playEnderEffect(Location l) {
		EnderDragon e = l.getWorld().spawn(l, EnderDragon.class);
		e.playEffect(EntityEffect.DEATH);
		e.setHealth(0);
	}
}
