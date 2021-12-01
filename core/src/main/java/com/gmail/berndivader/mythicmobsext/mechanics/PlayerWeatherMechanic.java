package com.gmail.berndivader.mythicmobsext.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

@ExternalAnnotation(name = "playerweather", author = "BerndiVader")
public class PlayerWeatherMechanic extends SkillMechanic implements ITargetedEntitySkill {
	int duration, time;
	WeatherType type;
	boolean relative;

	public PlayerWeatherMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
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
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isPlayer()) {
			Player p = (Player) target.getBukkitEntity();
			if (this.time > -1) {
				p.setPlayerTime(this.time, relative);
			}
			p.setPlayerWeather(this.type);
			new WeatherTracker(this.duration, p);
			return true;
		}
		return false;
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
