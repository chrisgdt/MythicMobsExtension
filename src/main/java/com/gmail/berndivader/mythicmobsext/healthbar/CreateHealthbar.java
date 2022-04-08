package com.gmail.berndivader.mythicmobsext.healthbar;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import org.bukkit.entity.LivingEntity;

public class CreateHealthbar extends SkillMechanic implements ITargetedEntitySkill {

	protected double offset;
	protected double hOffset;
	protected double vOffset;
	protected String display;
	protected int counter;
	protected boolean ignoreYaw;

	public CreateHealthbar(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.offset = mlc.getDouble(new String[] { "offset", "o" }, 2D);
		this.counter = mlc.getInteger(new String[] { "counter", "c" }, 200);
		this.hOffset = mlc.getDouble(new String[] { "sideoffset", "so" }, 0D);
		this.vOffset = mlc.getDouble(new String[] { "forwardoffset", "fo" }, 0D);
		this.ignoreYaw = mlc.getBoolean(new String[] { "ignoreyaw", "iy" }, false);
		String parse = mlc.getString(new String[] { "display", "text", "t" }, "$h");
		if (parse.startsWith("\"") && parse.endsWith("\"")) {
			parse = parse.substring(1, parse.length() - 1);
		}
		this.display = SkillString.parseMessageSpecialChars(parse);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!HealthbarHandler.healthbars.containsKey(target.getUniqueId()) && target.isLiving()) {
			LivingEntity entity = (LivingEntity) target.getBukkitEntity();
			new Healthbar(entity, this.offset, this.counter, this.display, this.hOffset, this.vOffset, this.ignoreYaw);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

}
