package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

@ExternalAnnotation(name = "infront", author = "BerndiVader")
public class InFrontCondition extends AbstractCustomCondition implements IEntityComparisonCondition {
	protected double viewAngle;

	public InFrontCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.viewAngle = MathUtils.round(mlc.getDouble(new String[] { "view", "angle", "v" }, 45.0D), 3);
	}

	@Override
	public boolean check(AbstractEntity source, AbstractEntity target) {
		Location s = source.getBukkitEntity().getLocation();
		Location t = target.getBukkitEntity().getLocation();
		double dT = Math.cos(this.viewAngle);
		Vector f = s.getDirection();
		Vector r = t.subtract(s).toVector().normalize();
		return Math.toDegrees(Math.asin(f.dot(r))) >= dT;
	}

}
