package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;

@ExternalAnnotation(name = "parsedstance,pstance", author = "BerndiVader")
public class ParsedStanceMechanic extends SkillMechanic
		implements ITargetedEntitySkill, ITargetedLocationSkill, INoTargetSkill {

	protected PlaceholderString stance;

	public ParsedStanceMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		String s = mlc.getString(new String[] { "stance", "s" });
		if (s.startsWith("\"") && s.endsWith("\""))
			s = s.substring(1, s.length() - 1);
		s = SkillString.parseMessageSpecialChars(s);
		this.stance = new PlaceholderStringImpl(s);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return castCast(data, data.getCaster().getEntity(), null);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		return castCast(data, target, null);
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
		return castCast(data, null, target);
	}

	private SkillResult castCast(SkillMetadata data, AbstractEntity e1, AbstractLocation l1) {
		if (Utils.mobmanager.isActiveMob(data.getCaster().getEntity())) {
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(data.getCaster().getEntity());
			String s = this.stance.get(data, e1);
			am.setStance(s);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
