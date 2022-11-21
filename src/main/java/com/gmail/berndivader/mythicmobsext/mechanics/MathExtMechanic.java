package com.gmail.berndivader.mythicmobsext.mechanics;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Objective;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.jboolexpr.MathInterpreter;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "mathex", author = "BerndiVader")
public class MathExtMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	PlaceholderString eval;
	String[] parse;
	HashMap<String, Double> variables;

	public MathExtMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		variables = new HashMap<>();
		String temp;
		if ((temp = mlc.getString(new String[] { "evaluate", "eval", "e" }, "")).startsWith("\"")) {
			temp = SkillString.unparseMessageSpecialChars(temp.substring(1, temp.length() - 1));
		}
		;
		eval = new PlaceholderStringImpl(temp);
		String s1 = mlc.getString(new String[] { "storage", "store", "s" }, "<mob.meta.test>");
		parse = (s1.substring(1, s1.length() - 1)).split(Pattern.quote("."));
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		return eval(data, target.getBukkitEntity(), null);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return eval(data, data.getCaster().getEntity().getBukkitEntity(),
				data.getCaster().getEntity().getBukkitEntity().getLocation());
	}

	SkillResult eval(SkillMetadata data, Entity e1, Location l1) {
		Entity target = null;
		double s1 = MathInterpreter.parse(this.eval.get(data, BukkitAdapter.adapt(e1)), variables).eval();
		switch (parse[0]) {
		case "mob":
		case "caster":
			target = data.getCaster().getEntity().getBukkitEntity();
			break;
		case "target":
			target = e1;
			break;
		case "trigger":
			target = data.getTrigger().getBukkitEntity();
			break;
		}
		if (target != null) {
			switch (parse[1]) {
			case "meta":
				target.setMetadata(parse[2], new FixedMetadataValue(Main.getPlugin(), s1));
				break;
			case "score":
				Objective o1 = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(parse[2]);
				if (o1 != null) {
					o1.getScore(target instanceof Player ? target.getName() : target.getUniqueId().toString())
							.setScore((int) s1);
					;
				} else {
					Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(parse[2], "dummy")
							.getScore(target instanceof Player ? target.getName() : target.getUniqueId().toString())
							.setScore((int) (s1));
				}
				break;
			case "stance":
				ActiveMob am = Utils.mobmanager.getMythicMobInstance(target);
				if (am != null)
					am.setStance(Double.toString(s1));
				break;
			}
		} else if (parse[0].equals("score")) {
			Objective o1 = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(parse[1]);
			if (o1 != null) {
				o1.getScore(parse[2]).setScore((int) (s1));
			} else {
				Bukkit.getScoreboardManager().getMainScoreboard().registerNewObjective(parse[1], "dummy")
						.getScore(parse[2]).setScore((int) (s1));
			}
		}
		return SkillResult.SUCCESS;
	}

}
