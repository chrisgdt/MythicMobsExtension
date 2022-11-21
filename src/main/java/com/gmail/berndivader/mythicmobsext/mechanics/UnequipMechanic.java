package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillExecutor;

import java.io.File;

@ExternalAnnotation(name = "unequip", author = "BerndiVader")
public class UnequipMechanic extends DamageArmorMechanic {

	public UnequipMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.rndMin = 9999;
		this.rndMax = 9999;
	}
}
