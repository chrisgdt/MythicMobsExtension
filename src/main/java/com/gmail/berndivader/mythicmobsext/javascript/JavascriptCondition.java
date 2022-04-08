package com.gmail.berndivader.mythicmobsext.javascript;

import javax.script.ScriptException;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillString;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;
import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;

@ExternalAnnotation(name = "jscondition", author = "BerndiVader")
public class JavascriptCondition extends AbstractCustomCondition implements IEntityCondition, ILocationCondition {
	boolean simple;
	String js = "print('Hello World!');";
	MythicLineConfig mlc;

	public JavascriptCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.mlc = mlc;
		simple = mlc.getBoolean("simple", false);
		String s1 = mlc.getString(new String[] { "js", "eval", "invok" }, js);
		js = SkillString.parseMessageSpecialChars(s1.substring(1, s1.length() - 1));
	}

	@Override
	public boolean check(AbstractLocation arg0) {
		return eval(mlc, null, BukkitAdapter.adapt(arg0));
	}

	@Override
	public boolean check(AbstractEntity arg0) {
		return eval(mlc, arg0.getBukkitEntity(), null);
	}

	private boolean eval(MythicLineConfig mlc, Entity e1, Location l1) {
		try {
			if(Nashorn.invocable!=null) {
				return (boolean) Nashorn.invocable.invokeFunction(js,
						e1 != null ? (Entity) e1 : l1 != null ? (Location) l1 : null, mlc);
			} else {
				Main.logger.warning("No javascript engine present!");
			}
		} catch (NoSuchMethodException | ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
