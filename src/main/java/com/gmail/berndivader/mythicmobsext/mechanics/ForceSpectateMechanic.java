package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import java.io.File;

@ExternalAnnotation(name = "forcespectate", author = "BerndiVader")
public class ForceSpectateMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private long d;
	public static String str = "mmSpectate";
	boolean shaderOnly;

	public ForceSpectateMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.d = mlc.getInteger(new String[] { "duration", "dur" }, 120);
		this.shaderOnly = mlc.getBoolean("shaderonly", false);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isPlayer() && (target.getBukkitEntity().getEntityId() == data.getCaster().getEntity()
				.getBukkitEntity().getEntityId())) {
			Player p = (Player) target.getBukkitEntity();
			Volatile.handler.forceSpectate(p, p, false);
			target.getBukkitEntity().removeMetadata(str, Main.getPlugin());
			return SkillResult.SUCCESS;
		}
		if (target.isPlayer() && !target.getBukkitEntity().hasMetadata(str)) {
			Player p = (Player) target.getBukkitEntity();
			p.setMetadata(str, new FixedMetadataValue(Main.getPlugin(), str));
			Volatile.handler.forceSpectate(p, data.getCaster().getEntity().getBukkitEntity(), this.shaderOnly);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (p != null && p.isOnline() && p.hasMetadata(str)) {
						Volatile.handler.forceSpectate(p, p, false);
						p.removeMetadata(str, Main.getPlugin());
					}
				}
			}.runTaskLaterAsynchronously(Main.getPlugin(), d);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
