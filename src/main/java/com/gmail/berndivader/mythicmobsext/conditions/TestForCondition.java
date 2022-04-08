package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillString;

@ExternalAnnotation(name = "testfor", author = "BerndiVader")
public class TestForCondition extends AbstractCustomCondition implements IEntityCondition {
	private String c;
	private char m = 0;

	public TestForCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.c = mlc.getString(new String[] { "vc", "c" }, "");
		if (c.startsWith("\"") && c.endsWith("\"")) {
			this.c = SkillString.parseMessageSpecialChars(this.c.substring(1, this.c.length() - 1));
		} else {
			this.c = c.replaceAll("\\(", "{").replaceAll("\\)", "}");
		}
	}

	@Override
	public boolean check(AbstractEntity e) {
		boolean b = true;
		b = Volatile.handler.testForCondition(e.getBukkitEntity(), this.c, m);
		return b;
	}

}
