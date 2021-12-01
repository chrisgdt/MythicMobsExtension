package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;

@ExternalAnnotation(name = "unequip", author = "BerndiVader")
public class UnequipMechanic extends DamageArmorMechanic {

	public UnequipMechanic(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.rndMin = 9999;
		this.rndMax = 9999;
	}
}
