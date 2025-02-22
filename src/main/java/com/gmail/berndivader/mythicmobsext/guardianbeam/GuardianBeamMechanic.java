package com.gmail.berndivader.mythicmobsext.guardianbeam;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

import java.io.File;


@ExternalAnnotation(name = "guardianbeam", author = "BerndiVader")
public class GuardianBeamMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
	int duration;
	double forwardOffset;
	double sideOffset;
	double yOffset;

	public GuardianBeamMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;

		duration = mlc.getInteger("duration", 1);
		forwardOffset = mlc.getDouble("forward", 1);
		sideOffset = mlc.getDouble("side", 0);
		yOffset = mlc.getDouble("yoffset", 1);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity e) {
		return cast(data, e.getLocation(), e.getBukkitEntity());
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation l) {
		return cast(data, l, null);

	}

	public SkillResult cast(SkillMetadata data, AbstractLocation l, Entity e) {

		final Entity caster = data.getCaster().getEntity().getBukkitEntity();
		final Location end = BukkitAdapter.adapt(l);

		Vector foV = MathUtils.getFrontBackOffsetVector(caster.getLocation().getDirection(), forwardOffset);
		final Beam beam = new Beam(caster.getLocation().add(foV).add(0, yOffset, 0), end);
		beam.start();

		new BukkitRunnable() {
			int t = 0;
			Location ol = caster.getLocation()
					.add(MathUtils.getFrontBackOffsetVector(caster.getLocation().getDirection(), forwardOffset))
					.add(0, yOffset, 0);

			@Override
			public void run() {
				if (t < duration) {
					Location l1 = caster.getLocation()
							.add(MathUtils.getFrontBackOffsetVector(caster.getLocation().getDirection(), forwardOffset))
							.add(0, yOffset, 0);
					Location l = ol.subtract(l1);
					l.setYaw(caster.getLocation().getYaw());
					l.setPitch(caster.getLocation().getPitch());
					beam.setStartingPosition(l);
					ol = l1;
				} else {
					if (beam.isActive())
						beam.stop();
					this.cancel();
				}
				t++;
			}
		}.runTaskTimer(Main.getPlugin(), 0l, 0l);

		return SkillResult.SUCCESS;
	}

}
