package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Handler;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "playerspin", author = "BerndiVader")
public class PlayerSpinMechanic extends SkillMechanic implements ITargetedEntitySkill {
	public static String str = "mmSpin";
	private long d;
	private float s;
	final private Handler vh = Volatile.handler;

	public PlayerSpinMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.d = (long) mlc.getInteger(new String[] { "duration", "dur" }, 120);
		this.s = mlc.getFloat(new String[] { "speed", "s" }, 30.0F);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!target.isPlayer())
			return SkillResult.CONDITION_FAILED;
		if (target.getBukkitEntity().hasMetadata(str))
			target.getBukkitEntity().removeMetadata(str, Main.getPlugin());
		final Entity entity = target.getBukkitEntity();
		final long d = this.d;
		final float s = this.s;
		entity.setMetadata(str, new FixedMetadataValue(Main.getPlugin(), true));
		new BukkitRunnable() {
			long c = 0;

			@Override
			public void run() {
				if (entity == null || entity.isDead() || c > d || !entity.hasMetadata(str)) {
					entity.removeMetadata(str, Main.getPlugin());
					this.cancel();
				} else {
					vh.playerConnectionSpin(entity, s);
				}
				c++;
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 1L, 1L);
		return SkillResult.SUCCESS;
	}

}
