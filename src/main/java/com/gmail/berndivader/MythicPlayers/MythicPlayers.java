package com.gmail.berndivader.MythicPlayers;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.MythicPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.gmail.berndivader.mythicmobsext.Main;

public class MythicPlayers {

	static String pluginName = "MythicPlayers";

	public static MythicPlugin mythicmobs;
	private Plugin plugin;
	private static MythicPlayers core;
	private PlayerManager playermanager;

	public MythicPlayers(Plugin plugin) {
		core = this;
		mythicmobs = Utils.mythicmobs;
		this.plugin = plugin;
		if (Bukkit.getServer().getPluginManager().getPlugin("MythicMobs") != null) {
			this.playermanager = new PlayerManager(this);
			this.plugin.getServer().getPluginManager().registerEvents(new MythicPlayerMythicMobsLoadEvent(),
					this.plugin);
		}
		Main.logger.info("using " + pluginName);
	}

	public static MythicPlayers inst() {
		return core;
	}

	public PlayerManager getPlayerManager() {
		return playermanager;
	}

	public Plugin plugin() {
		return this.plugin;
	}

}
