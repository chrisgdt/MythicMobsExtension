package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;

@ExternalAnnotation(name = "villager", author = "Seyarada")
public class VillagerMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private String profession;
	private int level;
	private String type;

	public VillagerMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		profession  = mlc.getString(new String[] { "profession", "p"}, null);
		level  = mlc.getInteger(new String[] { "level", "l"}, -1);
		type  = mlc.getString(new String[] { "type", "t"}, null);

	}
	
	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity e) {
		if (e.getBukkitEntity().getType().equals(EntityType.valueOf("VILLAGER"))) {
			Villager v = (Villager) e.getBukkitEntity();
			if (profession!=null) v.setProfession(Villager.Profession.valueOf(profession));
			if (level>0) v.setVillagerLevel(level);
			if (type!=null) v.setVillagerType(Villager.Type.valueOf(type));
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}