package com.gmail.berndivader.mythicmobsext.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "entitiesinradius,eir,leir,livingentitiesinradius,pir,playersinradius", author = "BerndiVader")
public class EntitiesInRadiusCondition extends AbstractCustomCondition implements ILocationCondition {
	private RangedDouble a;
	private double r;
	private boolean all = false, is, ignoreNPC;
	private char c;
	private List<String> mList = new ArrayList<String>();

	public EntitiesInRadiusCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.c = line.toUpperCase().charAt(0);
		String[] t = mlc.getString(new String[] { "entities", "entity", "types", "type", "t", "e" }, "ALL")
				.toUpperCase().split(",");
		if (t[0].equalsIgnoreCase("ALL") || c == 'P')
			this.all = true;
		this.a = new RangedDouble(mlc.getString(new String[] { "amount", "a" }, ">0"), false);
		this.r = mlc.getDouble(new String[] { "radius", "r" }, 5);
		this.is = mlc.getBoolean(new String[] { "ignoresameblock", "isb" }, false);
		this.ignoreNPC = mlc.getBoolean(new String[] { "ignorenpc", "npc" }, false);
		mList = Arrays.asList(t);
	}

	@Override
	public boolean check(AbstractLocation location) {
		Class<?> clazz;
		switch (c) {
		case 'L':
			clazz = LivingEntity.class;
			break;
		case 'P':
			clazz = Player.class;
			break;
		default:
			clazz = Entity.class;
			break;
		}
		return checkEntity(location, clazz);
	}

	private boolean checkEntity(AbstractLocation location, Class<?> clazz) {
		int count = 0;
		Location l = BukkitAdapter.adapt(location);
		for (Iterator<Entity> i1 = l.getWorld().getEntitiesByClasses(clazz).iterator(); i1.hasNext();) {
			Entity e = i1.next();
			if (!this.all && !mList.contains(e.getType().toString().toUpperCase()))
				continue;
			if (ignoreNPC && e.hasMetadata("NPC"))
				continue;
			Location el = e.getLocation();
			if (l.getWorld() != el.getWorld())
				continue;
			if (is && Utils.cmpLocByBlock(el, l))
				continue;
			double diffsq = l.distanceSquared(el);
			if (diffsq <= Math.pow(this.r, 2.0D))
				count++;
		}
		return this.a.equals(count);
	}
}
