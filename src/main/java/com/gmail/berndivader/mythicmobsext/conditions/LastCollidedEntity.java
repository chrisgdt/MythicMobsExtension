package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;;

@ExternalAnnotation(name = "lastcollided", author = "BerndiVader")
public class LastCollidedEntity extends AbstractCustomCondition implements IEntityCondition {
	String[] arr1;

	public LastCollidedEntity(String line, MythicLineConfig mlc) {
		super(line, mlc);
		arr1 = mlc.getString(new String[] { "types", "type", "t" }, "").toLowerCase().split(",");
	}

	@Override
	public boolean check(AbstractEntity entity) {
		Entity e;
		if ((e = entity.getBukkitEntity()).hasMetadata(Utils.meta_LASTCOLLIDETYPE)) {
			String s1 = e.getMetadata(Utils.meta_LASTCOLLIDETYPE).get(0).asString().toLowerCase();
			for (int i1 = 0; i1 < arr1.length; i1++) {
				if (s1.equals(arr1[i1]))
					return true;
			}
		}
		return false;
	}
}
