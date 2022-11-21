package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import java.io.File;
import java.util.Optional;

@ExternalAnnotation(name = "infect", author = "BerndiVader")
public class InfectWithMythic extends SkillMechanic implements ITargetedEntitySkill {

	private MythicMob mob_type;
	private int level;

	public InfectWithMythic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;

		Optional<MythicMob> opt = Utils.mobmanager.getMythicMob(mlc.getString("mobtype", ""));
		if (opt.isPresent()) {
			mob_type = opt.get();
		}
		level = mlc.getInteger("level", 1);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!target.isPlayer()) {
			ActiveMob am = infectEntity(target.getBukkitEntity(), data, mob_type, this.level);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	static ActiveMob infectEntity(Entity entity, SkillMetadata data, MythicMob mob_type, int level) {
		ActiveMob am = new ActiveMob(BukkitAdapter.adapt(entity), mob_type, level);
		if (am != null) {
			if (mob_type.hasFaction()) {
				am.setFaction(mob_type.getFaction());
				am.getEntity().getBukkitEntity().setMetadata("Faction",
						new FixedMetadataValue(Utils.mythicmobs, mob_type.getFaction()));
			}
			//Utils.mobmanager.registerActiveMob(am);
			new TriggeredSkill(SkillTriggers.SPAWN, am, data.getCaster().getEntity(), true);
		}
		return am;
	}

}
