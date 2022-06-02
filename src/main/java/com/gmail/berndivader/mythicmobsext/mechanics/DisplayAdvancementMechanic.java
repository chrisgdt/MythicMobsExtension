package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "sendtoast", author = "BerndiVader")
public class DisplayAdvancementMechanic extends SkillMechanic implements ITargetedEntitySkill {
	Material material;
	PlaceholderString message;
	String frame;

	public DisplayAdvancementMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		material = Material.STONE;
		try {
			material = Material.valueOf(mlc.getString(new String[] { "icon", "material" }, "stone").toUpperCase());
		} catch (Exception e) {
			Main.logger.warning("Wrong material type. Set to stone");
		}
		message = mlc.getPlaceholderString("message", "Title");
		frame = mlc.getString("frame", "GOAL").toUpperCase();
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity a_target) {
		if (!a_target.isPlayer())
			return SkillResult.CONDITION_FAILED;
		String msg = this.message.get(data, a_target);
		Volatile.handler.sendPlayerAdvancement((Player) a_target.getBukkitEntity(), material, msg, "", frame);
		return SkillResult.SUCCESS;
	}

}
