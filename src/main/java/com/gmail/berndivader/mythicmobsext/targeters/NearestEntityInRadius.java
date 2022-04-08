package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "neir,nearestentityinradius", author = "BerndiVader")
public class NearestEntityInRadius extends ISelectorEntity {
	String[] ml;
	boolean all = false, origin;
	byte source_type;
	double r;

	public NearestEntityInRadius(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
		ml = mlc.getString(new String[] { "mobtypes", "types", "mobs", "mob", "type", "t", "m" }, "ALL").toUpperCase()
				.split(",");
		if (ml.length == 1 && (ml[0].equals("ALL") || ml[0].equals("ANY"))) {
			this.all = true;
		}
		this.r = Math.pow(mlc.getDouble(new String[] { "radius", "r" }, 32), 2d);
		this.origin = mlc.getBoolean("origin", false);
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		double radius = this.r;
		Entity nearest = null;
		UUID id = data.getCaster().getEntity().getUniqueId();
		HashSet<AbstractEntity> targets = new HashSet<>();
		Location l = BukkitAdapter.adapt(origin ? data.getOrigin() : data.getCaster().getLocation());
		double nearest_distance = 0d;
		for (Iterator<LivingEntity> it = l.getWorld().getLivingEntities().iterator(); it.hasNext();) {
			Entity e = it.next();
			if (e.getUniqueId().equals(id) || e.getWorld() != l.getWorld())
				continue;
			double diffsq = l.distanceSquared(e.getLocation());
			if (diffsq <= radius) {
				if (this.all) {
					if (nearest == null || diffsq < nearest_distance) {
						nearest = e;
						nearest_distance = diffsq;
					}
				} else {
					for (String s1 : ml) {
						if (s1.equals(e.getType().toString().toUpperCase())) {
							if (nearest == null || diffsq < nearest_distance) {
								nearest = e;
								nearest_distance = diffsq;
							}
							break;
						}
					}
				}
			}
		}
		if (nearest != null)
			targets.add(BukkitAdapter.adapt(nearest));
		return this.applyOffsets(targets);
	}

}
