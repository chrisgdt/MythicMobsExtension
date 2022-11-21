package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import java.io.File;

@ExternalAnnotation(name = "playerzoom", author = "BerndiVader")
public class PlayerZoomMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private Float f1;

	public PlayerZoomMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		f(mlc.getFloat(new String[] { "value", "v", "amount", "a" }, 0.0F));
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isPlayer()) {
			Volatile.handler.setFieldOfViewPacketSend((Player) target.getBukkitEntity(), this.f1);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	private void f(Float f1) {
		this.f1 = f1 > 1.0f ? 1.0f : f1 < 0.0f ? 0.0f : f1;
	}

}
