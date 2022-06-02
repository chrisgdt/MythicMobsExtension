package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "delmeta", author = "BerndiVader")
public class DeleteMetatagMechanic extends SetMetatagMechanic {

	public DeleteMetatagMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		String parsedTag = this.tag.get(data, target);
		if (parsedTag == null || parsedTag.isEmpty())
			return SkillResult.CONDITION_FAILED;
		Entity bEntity = target.getBukkitEntity();
		if (bEntity.hasMetadata(parsedTag)) {
			bEntity.removeMetadata(parsedTag, Main.getPlugin());
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation location) {
		String parsedTag = this.tag.get(data);
		if (parsedTag == null || parsedTag.isEmpty())
			return SkillResult.CONDITION_FAILED;
		Block target = BukkitAdapter.adapt(location).getBlock();
		if (target.hasMetadata(parsedTag)) {
			target.removeMetadata(parsedTag, Main.getPlugin());
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
