package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import java.io.File;

@ExternalAnnotation(name = "setnbt", author = "BerndiVader")
public class SetNbtMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	PlaceholderString s1;

	public SetNbtMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager,file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		String tmp = mlc.getString("nbt");
		if (tmp.startsWith("\"") && tmp.endsWith("\"")) {
			tmp = SkillString.parseMessageSpecialChars(tmp.substring(1, tmp.length() - 1));
		} else {
			tmp = "{}";
		}
		s1 = new PlaceholderStringImpl(tmp);
	}

	@Override
	public SkillResult cast(SkillMetadata var1) {
		Entity e = var1.getCaster().getEntity().getBukkitEntity();
		return setNbt(e, s1.get(var1));
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata var1, AbstractEntity var2) {
		Entity e = var2.getBukkitEntity();
		return setNbt(e, s1.get(var1, var2));
	}

	SkillResult setNbt(Entity e1, String s1) {
		if (Volatile.handler.addNBTTag(e1, s1)) {
			return SkillResult.SUCCESS;
		} else {
			return SkillResult.CONDITION_FAILED;
		}
	}

}
