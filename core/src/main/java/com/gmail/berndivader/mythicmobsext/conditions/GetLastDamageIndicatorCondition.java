package com.gmail.berndivader.mythicmobsext.conditions;

import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "lastindicator,lastdamageindicator,getlastindicator,getlastdamageindicator", author = "BerndiVader")
public class GetLastDamageIndicatorCondition extends AbstractCustomCondition implements IEntityCondition {
	private RangedDouble rd;
	public static String meta_LASTDAMAGEINDICATOR = "MMEXTINDICATOR";

	public GetLastDamageIndicatorCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		rd(mlc.getString("value", ">0"));
	}

	@Override
	public boolean check(AbstractEntity e) {
		if (e.isPlayer()) {
			Player p = (Player) e.getBukkitEntity();
			if (p.hasMetadata(meta_LASTDAMAGEINDICATOR)) {
				float f1 = p.getMetadata(meta_LASTDAMAGEINDICATOR).get(0).asFloat();
				return this.rd.equals((double) f1);
			}
		}
		return true;
	}

	private void rd(String s1) {
		this.rd = new RangedDouble(s1);
	}

}
