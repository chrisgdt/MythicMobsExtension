package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.FacingDirectionType;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;

@ExternalAnnotation(name = "facingdirection", author = "BerndiVader")
public class FacingDirectionCondition extends AbstractCustomCondition implements IEntityCondition {
	FacingDirectionType t;

	public FacingDirectionCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.t = FacingDirectionType
				.get(mlc.getString(new String[] { "direction", "dir", "d", "facing", "face", "f" }, "NORTH"));
	}

	@Override
	public boolean check(AbstractEntity entity) {
		FacingDirectionType fd = FacingDirectionType
				.getFacingDirection(Math.abs((double) entity.getBukkitEntity().getLocation().getYaw()));
		return this.t.equals(fd);
	}

}
