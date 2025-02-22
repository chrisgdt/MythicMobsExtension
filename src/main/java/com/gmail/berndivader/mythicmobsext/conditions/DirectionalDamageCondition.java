package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import org.bukkit.util.Vector;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "relativedirection", author = "BerndiVader")
public class DirectionalDamageCondition extends AbstractCustomCondition implements IEntityComparisonCondition {
	private RangedDouble angle;

	public DirectionalDamageCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		String a = mlc.getString("angle", null);
		if (a != null)
			this.angle = new RangedDouble(a);
	}

	@Override
	public boolean check(AbstractEntity s, AbstractEntity t) {
		if (this.angle == null)
			return false;
		Vector Vvp = s.getBukkitEntity().getLocation().toVector().setY(0);
		Vector Vap = t.getBukkitEntity().getLocation().toVector().setY(0);
		Vector Vvd = s.getBukkitEntity().getLocation().getDirection().setY(0);
		Vector Vd = Vap.subtract(Vvp).normalize();
		Vector VDD = Vd.clone();
		int a = (int) Math.toDegrees(Math.acos(Vd.dot(Vvd)));
		a = VDD.crossProduct(Vvd.multiply(2D).normalize()).getY() < 0.0d ? -a : a;
		return this.angle.equals((a + 450) % 360);
	}
}
