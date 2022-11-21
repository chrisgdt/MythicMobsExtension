package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.MetaTagValue;
import com.gmail.berndivader.mythicmobsext.utils.MetaTagValue.ValueTypes;

import java.io.File;

@ExternalAnnotation(name = "setmeta", author = "BerndiVader")
public class SetMetatagMechanic extends SkillMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
	protected PlaceholderString tag;
	protected MetaTagValue mtv;
	protected boolean useCaster;

	public SetMetatagMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.useCaster = mlc.getBoolean(new String[] { "usecaster", "uc" }, false);
		String ms = mlc.getString(new String[] { "meta", "m" }, "");
		if (ms.startsWith("\"") && ms.endsWith("\""))
			ms = ms.substring(1, ms.length() - 1);
		ms = SkillString.parseMessageSpecialChars(ms);
		String parse[] = ms.split(";");
		String t = null, v = null, vt = null;
		for (int a = 0; a < parse.length; a++) {
			String p = parse[a];
			if (p.startsWith("tag=")) {
				t = p.substring(4);
			} else if (p.startsWith("value=")) {
				v = p.substring(6);
			} else if (p.startsWith("type=")) {
				vt = p.substring(5);
			}
		}
		if (t != null) {
			this.tag = new PlaceholderStringImpl(t);
			mtv = new MetaTagValue(v, vt);
		}
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		String parsedTag = this.tag.get(data, target);
		if (parsedTag == null || parsedTag.isEmpty())
			return SkillResult.CONDITION_FAILED;
		Object vo = this.mtv.getType().equals(ValueTypes.STRING)
				? new PlaceholderStringImpl((String) this.mtv.getValue()).get(data, target)
				: this.mtv.getValue();
		if (this.useCaster) {
			data.getCaster().getEntity().getBukkitEntity().setMetadata(parsedTag,
					new FixedMetadataValue(Main.getPlugin(), vo));
		} else {
			target.getBukkitEntity().setMetadata(parsedTag, new FixedMetadataValue(Main.getPlugin(), vo));
		}
		return SkillResult.SUCCESS;
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation location) {
		Block target = BukkitAdapter.adapt(location).getBlock();
		String parsedTag = this.tag.get(data);
		if (parsedTag == null || parsedTag.isEmpty())
			return SkillResult.CONDITION_FAILED;
		Object vo = this.mtv.getType().equals(ValueTypes.STRING)
				? new PlaceholderStringImpl((String) this.mtv.getValue()).get(data)
				: this.mtv.getValue();
		target.setMetadata(parsedTag, new FixedMetadataValue(Main.getPlugin(), vo));
		return SkillResult.SUCCESS;
	}
}
