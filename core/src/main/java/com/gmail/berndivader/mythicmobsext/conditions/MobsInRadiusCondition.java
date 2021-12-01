package com.gmail.berndivader.mythicmobsext.conditions;

import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.conditions.ILocationCondition;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "mir,mobsinradius", author = "BerndiVader")
public class MobsInRadiusCondition extends AbstractCustomCondition implements ILocationCondition {
	private RangedDouble a;
	private double r;
	private boolean all = false;
	private String[] ml;

	public MobsInRadiusCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		ml = mlc.getString(new String[] { "mobtypes", "types", "mobs", "mob", "type", "t", "m" }, "ALL").toUpperCase()
				.split(",");
		this.all = ml.length == 1 && (ml[0].equals("ALL") || ml[0].equals("ANY"));
		this.a = new RangedDouble(mlc.getString(new String[] { "amount", "a" }, "0"), false);
		this.r = mlc.getDouble(new String[] { "radius", "r" }, 5);
	}

	@Override
	public boolean check(AbstractLocation location) {
		int count = 0;
		Location l = BukkitAdapter.adapt(location);
		ListIterator<LivingEntity> it = l.getWorld().getLivingEntities().listIterator();
		while (it.hasNext()) {
			LivingEntity e = it.next();
			Location el = e.getLocation();
			if (!el.getWorld().equals(l.getWorld()))
				continue;
			double diffsq = l.distanceSquared(el);
			if (diffsq <= Math.pow(this.r, 2.0D)) {
				ActiveMob am = Utils.mobmanager.getMythicMobInstance(e);
				if (am == null)
					continue;
				if (this.all) {
					count++;
				} else {
					for (String s1 : ml) {
						if (s1.equals(am.getType().getInternalName().toUpperCase()))
							count++;
					}
				}
				am = null;
			}
		}
		return this.a.equals((double) count);
	}

}
