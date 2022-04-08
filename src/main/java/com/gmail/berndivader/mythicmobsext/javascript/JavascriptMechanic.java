package com.gmail.berndivader.mythicmobsext.javascript;

import javax.script.ScriptException;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "jsmechanic", author = "BerndiVader")
public class JavascriptMechanic extends SkillMechanic
		implements INoTargetSkill, ITargetedEntitySkill, ITargetedLocationSkill {
	boolean simple;
	String js = "print('Hello World!');";
	MythicLineConfig mlc;

	public JavascriptMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.mlc = mlc;
		simple = mlc.getBoolean("simple", false);
		String s1 = mlc.getString(new String[] { "js", "eval", "invok" }, js);
		js = SkillString.parseMessageSpecialChars(s1.substring(1, s1.length() - 1));
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
		return eval(data, null, BukkitAdapter.adapt(target));
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		return eval(data, target.getBukkitEntity(), null);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return eval(data, null, null);
	}

	private SkillResult eval(SkillMetadata data, Entity e1, Location l1) {
		try {
			if(Nashorn.invocable!=null) {
				Nashorn.invocable.invokeFunction(js, data, e1 != null ? (Entity) e1 : l1 != null ? (Location) l1 : null,
						mlc);
			}else {
				Main.logger.warning("No javascriptengine found!");
			}
		} catch (NoSuchMethodException | ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SkillResult.SUCCESS;
	}
}
