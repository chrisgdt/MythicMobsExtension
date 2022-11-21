package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;

import java.io.File;

@ExternalAnnotation(name = "modifyarrows", author = "BerndiVader")
public class ModifyArrowsMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private int a;
	private char m;

	public ModifyArrowsMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.m = mlc.getString(new String[] { "mode", "m" }, "c").toUpperCase().charAt(0);
		this.a = mlc.getInteger(new String[] { "amount", "a" }, 1);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isLiving()) {
			modifyArrowsAtEntity(target.getBukkitEntity(), this.a, this.m);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	/*
	 * @param entity {@link LivingEntity}
	 * @param amount {@link Integer}
	 * @param action {@link Char} <b>A</b>=add - <b>S</b>=sub - <b>C</b>=clear
	 */
	static void modifyArrowsAtEntity(Entity entity, int amount, char action) {
		int a = NMSUtils.getArrowsOnEntity((LivingEntity) entity);
		switch (action) {
		case 'A':
			amount += a;
			break;
		case 'S':
			amount = a - amount;
			if (amount < 0)
				a = 0;
			break;
		case 'C':
			amount = 0;
			break;
		}
		NMSUtils.setArrowsOnEntity((LivingEntity) entity, amount);
	}

}
