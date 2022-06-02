package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;

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

@ExternalAnnotation(name = "custommessage,mmemessage,sendmessage", author = "BerndiVader")
public class MessageMechanic extends SkillMechanic implements ITargetedEntitySkill {
	PlaceholderString msg;

	public MessageMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.target_creative = true;
		String tmp = mlc.getString(new String[] { "msg", "m" }, null);
		if (tmp != null && (tmp.startsWith("\"") && tmp.endsWith("\""))) {
			tmp = tmp.substring(1, tmp.length() - 1);
			tmp = SkillString.parseMessageSpecialChars(tmp);
		} else {
			tmp = "Invalid msg format in config of: " + line;
		}
		msg = new PlaceholderStringImpl(tmp);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity e1) {
		if (e1.isPlayer())
			e1.getBukkitEntity().sendMessage(this.msg.get(data, e1));
		return SkillResult.SUCCESS;
	}
}
