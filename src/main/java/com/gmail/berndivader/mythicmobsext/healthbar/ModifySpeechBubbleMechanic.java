package com.gmail.berndivader.mythicmobsext.healthbar;

import java.io.File;
import java.util.ArrayList;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;

public class ModifySpeechBubbleMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private String text;
	private String id;
	private String s1;
	private double offset;
	private double so;
	private double fo;
	private int ll;
	private int i1;

	public ModifySpeechBubbleMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.id = mlc.getString("id", "bubble");
		this.offset = mlc.getDouble(new String[] { "offset", "yo" }, -999);
		this.so = mlc.getDouble(new String[] { "sideoffset", "so" }, -999);
		this.fo = mlc.getDouble(new String[] { "forwardoffset", "fo" }, -999);
		this.ll = mlc.getInteger(new String[] { "linelength", "ll" }, 20);
		this.i1 = mlc.getInteger(new String[] { "counter", "time", "c" }, -999);
		this.s1 = mlc.getString(new String[] { "usecounter", "uc" }, "").toLowerCase();
		this.text = mlc.getString(new String[] { "display", "text", "t" }, null);
		if (text != null) {
			if (text.startsWith("\"") && text.endsWith("\"")) {
				this.text = text.substring(1, text.length() - 1);
			}
		}
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (HealthbarHandler.speechbubbles
				.containsKey(data.getCaster().getEntity().getUniqueId().toString() + this.id)) {
			SpeechBubble sb = HealthbarHandler.speechbubbles
					.get(data.getCaster().getEntity().getUniqueId().toString() + this.id);
			if (this.text != null) {
				String s1 = this.text;
				s1 = SkillString.unparseMessageSpecialChars(s1);
				s1 = Utils.parseMobVariables(s1, data.getCaster(), target, data.getTrigger());
				ArrayList<String> li1 = new ArrayList<String>();
				String[] a2 = s1.split("<nl>");
				for (int i = 0; i < a2.length; i++) {
					String[] a4 = Utils.wrapStr(a2[i], ll);
					for (int i1 = 0; i1 < a4.length; i1++) {
						li1.add(a4[i1]);
					}
				}
				sb.template = li1.toArray(new String[li1.size()]);
				sb.lines();
			}
			if (this.offset != -999)
				sb.offset = this.offset;
			if (this.so != -999) {
				sb.sOffset = this.so;
				sb.useOffset = true;
			}
			if (this.fo != -999) {
				sb.fOffset = this.fo;
				sb.useOffset = true;
			}
			if (this.i1 != -999)
				sb.counter = this.i1;
			i(sb);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	private void i(SpeechBubble sb) {
		switch (this.s1) {
		case "true": {
			sb.uc1 = true;
			break;
		}
		case "false": {
			sb.uc1 = false;
			break;
		}
		}
	}
}
