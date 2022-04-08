package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "setrotation", author = "BerndiVader")
public class SetRotationMechanic extends SkillMechanic implements ITargetedEntitySkill {
	public static String str = "mmRotate";
	private float yawOff;
	private float pitchOff;
	private long d;

	public SetRotationMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.yawOff = mlc.getFloat(new String[] { "yawoffset", "yaw", "yo", "y" }, 5.0F);
		this.pitchOff = mlc.getFloat(new String[] { "pitchoffset", "pitch", "po", "p" }, 0F);
		this.d = mlc.getLong(new String[] { "duration", "dur" }, 1);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isPlayer())
			return SkillResult.CONDITION_FAILED;
		if (target.getBukkitEntity().hasMetadata(str))
			target.getBukkitEntity().removeMetadata(str, Main.getPlugin());
		final float yo = this.yawOff, po = this.pitchOff;
		final long d = this.d;
		target.getBukkitEntity().setMetadata(str, new FixedMetadataValue(Main.getPlugin(), true));
		new BukkitRunnable() {
			float yaw = target.getBukkitEntity().getLocation().getYaw();
			float pitch = target.getBukkitEntity().getLocation().getPitch();
			long c = 0;

			@Override
			public void run() {
				if (c > d || target.isDead() || !target.getBukkitEntity().hasMetadata(str)) {
					if (!target.isDead()) {
						target.getBukkitEntity().removeMetadata(str, Main.getPlugin());
					}
					this.cancel();
				} else {
					yaw = MathUtils.normalise(yaw + yo, 0, 360);
					pitch = MathUtils.normalise(pitch + po, 0, 360);
					Volatile.handler.rotateEntityPacket(target.getBukkitEntity(), yaw, pitch);
				}
				c++;
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 1L, 1L);
		return SkillResult.SUCCESS;
	}
}
