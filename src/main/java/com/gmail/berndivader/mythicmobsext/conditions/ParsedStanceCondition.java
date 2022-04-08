package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;

@ExternalAnnotation(name = "parsedstance,pstance", author = "BerndiVader")
public class ParsedStanceCondition extends AbstractCustomCondition implements IEntityComparisonCondition {
	private PlaceholderString stance;
	private boolean compareToSelf;

	public ParsedStanceCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		String temp = mlc.getString(new String[] { "stance", "s" });
		this.compareToSelf = mlc.getBoolean(new String[] { "compareself", "cs" }, false);
		if (temp != null && (temp.startsWith("\"") && temp.endsWith("\""))) {
			temp = temp.substring(1, temp.length() - 1);
		}
		this.stance = new PlaceholderStringImpl(temp);
	}

	@Override
	public boolean check(AbstractEntity caster, AbstractEntity ae) {
		ActiveMob am = Utils.mobmanager.getMythicMobInstance(caster);
		SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, am, ae);
		String stance = this.stance.get(data, ae);
		ActiveMob target = this.compareToSelf ? am : Utils.mobmanager.getMythicMobInstance(ae);
		return target.getStance().contains(stance);
	}

}
