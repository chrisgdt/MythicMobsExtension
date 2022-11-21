package com.gmail.berndivader.mythicmobsext.mechanics;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Vec2D;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "forcedirection", author = "BerndiVader")
public class ForceDirectionMechanic extends SkillMechanic implements ITargetedEntitySkill {
	BlockFace faceing;
	long duration;
	double noise;

	public ForceDirectionMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		faceing = BlockFace.valueOf(mlc.getString("faceing", "north").toUpperCase());
		duration = mlc.getInteger("duration", 1);
		noise = mlc.getDouble("noise", 0.0d);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!target.isPlayer())
			return SkillResult.CONDITION_FAILED;
		Player p = (Player) target.getBukkitEntity();
		Location source = p.getEyeLocation();
		double dx = 0, dz = 0;
		switch (faceing) {
		case NORTH:
			dz = -1;
			break;
		case SOUTH:
			dz = 1;
			break;
		case WEST:
			dx = -1;
			break;
		case EAST:
			dx = 1;
			break;
		default:
			break;
		}
		final Location dd = source.clone().add(dx, 0, dz);
		final long d = duration;
		final double noise = this.noise;
		new BukkitRunnable() {
			long count = 0;

			@Override
			public void run() {
				double dx = 0, dy = 0, dz = 0;
				if (p == null || p.isDead() || count > d) {
					this.cancel();
				} else {
					if (noise > 0.0d) {
						ThreadLocalRandom r = ThreadLocalRandom.current();
						dx += r.nextDouble(noise * -1, noise);
						dy += r.nextDouble(noise * -1, noise);
						dz += r.nextDouble(noise * -1, noise);
					}
					Vec2D v = MathUtils.lookAtVec(p.getEyeLocation(), dd.add(dx, dy, dz));
					Volatile.handler.playerConnectionLookAt(p, (float) v.getX(), (float) v.getY());
				}
				dd.subtract(dx, dy, dz);
				count++;
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 1L, 1L);
		return SkillResult.SUCCESS;
	}

}
