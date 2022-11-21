package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;

import java.io.File;

@ExternalAnnotation(name = "renameentity", author = "BerndiVader")
public class RenameEntityMechanic extends SkillMechanic implements ITargetedEntitySkill {
	PlaceholderString name;
	boolean v;

	public RenameEntityMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = line;
		String tmp = mlc.getString(new String[] { "name", "n" }, "");
		if (tmp.charAt(0) == '"' && tmp.charAt(tmp.length() - 1) == '"') {
			tmp = tmp.substring(1, tmp.length() - 1);
		}
		name = new PlaceholderStringImpl(SkillString.parseMessageSpecialChars(tmp));
		this.v = mlc.getBoolean(new String[] { "visible", "v" }, false);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isLiving() && !target.isPlayer() && this.name != null) {
			String n = this.name.get(data, target);
			LivingEntity e = (LivingEntity) target.getBukkitEntity();
			e.setCustomName(n);
			e.setCustomNameVisible(this.v);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
