package com.gmail.berndivader.mythicmobsext.mechanics.customprojectiles;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

@ExternalAnnotation(name = "modifyprojectile", author = "BerndiVader")
public class ModifyProjectile extends SkillMechanic implements IParentSkill {

	public ModifyProjectile(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean getCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCancelled() {
		// TODO Auto-generated method stub

	}

}
