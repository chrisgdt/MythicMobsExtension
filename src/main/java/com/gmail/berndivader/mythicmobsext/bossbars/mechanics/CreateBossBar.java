package com.gmail.berndivader.mythicmobsext.bossbars.mechanics;

import java.util.ArrayList;
import java.util.List;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.bossbars.BossBars;
import com.gmail.berndivader.mythicmobsext.bossbars.SegmentedEnum;

public class CreateBossBar extends SkillMechanic implements ITargetedEntitySkill {
	PlaceholderString title, expr;
	BarStyle style;
	BarColor color;
	List<BarFlag> flags;
	int flags_size;

	public CreateBossBar(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		title = mlc.getPlaceholderString("title", "Bar");
		style = BarStyle.valueOf(SegmentedEnum.real(mlc.getInteger("segment", 6)).name());
		expr = mlc.getPlaceholderString("value", "0.05d");
		try {
			color = BarColor.valueOf(mlc.getString("color", "RED").toUpperCase());
		} catch (Exception ex) {
			Main.logger.info(mlc.getString("color") + " is not valid for BarColor in " + skill);
			color = BarColor.RED;
		}
		flags = new ArrayList<>();
		String[] arr = mlc.getString("flags", "").toUpperCase().split(",");
		int size = arr.length;
		for (int i1 = 0; i1 < size; i1++) {
			String parse = arr[i1];
			if (!parse.isEmpty()) {
				BarFlag flag = null;
				try {
					flag = BarFlag.valueOf(parse);
				} catch (Exception ex) {
					Main.logger.info(parse + " is no valid BarFlag in " + skill);
				}
				if (flag != null)
					flags.add(flag);
			}
		}
		flags_size = flags.size();
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		if (abstract_entity.isPlayer()) {
			Player player = (Player) abstract_entity.getBukkitEntity();
			BossBar bar = Bukkit.createBossBar(title.get(data, abstract_entity), color, style);
			if (bar != null) {
				double default_value = 1d;
				String parsed_expr = expr.get(data, abstract_entity);
				try {
					default_value = Double.parseDouble(parsed_expr);
				} catch (Exception e) {
					Main.logger.info(parsed_expr + " is not valid for double in " + this.line);
					default_value = 0d;
				}
				bar.setProgress(default_value);
				for (int i1 = 0; i1 < flags_size; i1++) {
					bar.addFlag(flags.get(i1));
				}
				BossBars.addBar(abstract_entity.getUniqueId(), bar);
				bar.addPlayer(player);
				return SkillResult.SUCCESS;
			}
		}
		return SkillResult.CONDITION_FAILED;
	}

}
