package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import java.io.File;

@ExternalAnnotation(name = "setfaction", author = "BerndiVader")
public class SetFactionMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	protected PlaceholderString faction;

	public SetFactionMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		String f = mlc.getString(new String[] { "faction", "f" }, null);
		if (f != null) {
			this.faction = new PlaceholderStringImpl(SkillString.unparseMessageSpecialChars(f));
		}
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return this.castAtEntity(data, data.getCaster().getEntity());
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		ActiveMob am = Utils.mobmanager.getMythicMobInstance(target);
		String f = this.faction.get(data, target);
		if (am != null)
			am.setFaction(f);
		target.getBukkitEntity().setMetadata("Faction", new FixedMetadataValue(Utils.mythicmobs, f));
		return SkillResult.SUCCESS;
	}
}
