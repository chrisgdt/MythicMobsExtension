package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "playerweather", author = "BerndiVader")
public class PlayerWeatherMechanic extends SkillMechanic implements ITargetedEntitySkill {
	int duration, time;
	WeatherType type;
	boolean relative;

	public PlayerWeatherMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		try {
			this.type = WeatherType.valueOf(mlc.getString(new String[] { "weather", "w" }, "CLEAR").toUpperCase());
		} catch (Exception e) {
			this.type = WeatherType.CLEAR;
		}
		this.time = mlc.getInteger(new String[] { "time", "t" }, -1);
		this.duration = mlc.getInteger(new String[] { "duration", "dur" }, 200);
		relative = mlc.getBoolean("relative", false);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isPlayer()) {
			Player p = (Player) target.getBukkitEntity();
			if (this.time > -1) {
				p.setPlayerTime(this.time, relative);
			}
			p.setPlayerWeather(this.type);
			new WeatherTracker(this.duration, p);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	private class WeatherTracker implements Runnable {
		private Player p;

		public WeatherTracker(int duration, Player player) {
			this.p = player;
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(), this, duration);
		}

		@Override
		public void run() {
			this.p.resetPlayerWeather();
			this.p.resetPlayerTime();
		}
	}

}
