package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.Vec2D;
import com.gmail.berndivader.mythicmobsext.utils.Vec3D;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "bowaimbot", author = "BerndiVader")
public class AimBowMechanic extends SkillMechanic implements ITargetedEntitySkill {
	static String meta_str;

	static {
		meta_str = "MMEAIMBOT";
	}

	public AimBowMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity a_target) {
		if (data.getCaster().getEntity().isPlayer() && a_target.isLiving()) {
			final Player player = (Player) data.getCaster().getEntity().getBukkitEntity();

			if (!player.hasMetadata(meta_str)) {
				final LivingEntity target = (LivingEntity) a_target.getBukkitEntity();
				player.setMetadata(meta_str, new FixedMetadataValue(Main.getPlugin(), true));

				new BukkitRunnable() {

					@Override
					public void run() {
						if (player.isDead() || !player.isOnline() || !player.isHandRaised() || !player.hasMetadata(meta_str)) {
							if (player.hasMetadata(meta_str))
								player.removeMetadata(meta_str, Main.getPlugin());
							this.cancel();
						} else {
							float velocity = Utils.getBowTension(player);
							if (velocity > 0.1f) {
								Vec3D target_position = Volatile.handler.getPredictedMotion(player, target, 1.0f);
								Vec2D direction = MathUtils.calculateDirectionVec2D(target_position, velocity, 0.006f);
								float yaw = (float) direction.getX();
								float pitch = (float) direction.getY();
								if (!Float.isNaN(yaw) && !Float.isNaN(pitch))
									Volatile.handler.playerConnectionLookAt(player, yaw, pitch);
							}
						}
					}
				}.runTaskTimer(Main.getPlugin(), 1l, 1l);
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

}
