package com.gmail.berndivader.mythicmobsext.compatibility.quests;

import java.util.Map;
import java.util.Optional;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import me.pikamug.quests.BukkitQuestsPlugin;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.gmail.berndivader.mythicmobsext.Main;

public class QuestsSupport implements Listener {
	static String pluginName = "Quests";
	private static QuestsSupport core;
	private Plugin plugin;
	private static Optional<BukkitQuestsPlugin> quests;

	static {
		quests = Optional.ofNullable((BukkitQuestsPlugin) Bukkit.getServer().getPluginManager().getPlugin(pluginName));
	}

	public QuestsSupport(Plugin plugin) {
		if (Bukkit.getPluginManager().getPlugin(quests.get().getName()).getDescription().getVersion().contains("5.1")) {
			core = this;
			this.plugin = plugin;
			Main.pluginmanager.registerEvents(this, plugin);
			Main.logger.info("using " + pluginName);
		} else {
			Main.logger.warning("Your Quests plugin version is too old, update to version 5.1.x it if you want to use it!");
		}

	}

	public static QuestsSupport inst() {
		return core;
	}

	public Plugin plugin() {
		return this.plugin;
	}

	public static boolean isPresent() {
		return quests.isPresent();
	}

	public BukkitQuestsPlugin quests() {
		return QuestsSupport.quests.get();
	}

	@EventHandler
	public void onQuestConditionsLoad(MythicConditionLoadEvent e) {
		String s1 = e.getConditionName().toLowerCase();
		switch (s1) {
		case "activequest":
		case "activequest_ext": {
			e.register(new QuestRunningCondition(e.getConditionName(), e.getConfig()));
			break;
		}
		case "completedquest":
		case "completedquest_ext": {
			e.register(new QuestCompleteCondition(e.getConditionName(), e.getConfig()));
			break;
		}
		case "testrequirement":
		case "testrequirement_ext": {
			e.register(new TestRequirementCondition(e.getConditionName(), e.getConfig()));
			break;
		}
		}
	}

	@EventHandler
	public void onQuestMechanicsLoad(MythicMechanicLoadEvent e) {
		String s1 = e.getMechanicName().toLowerCase();
		switch (s1) {
		case "completequest":
		case "takequest":
		case "failquest":
		case "completequest_ext":
		case "takequest_ext":
		case "failquest_ext": {
			e.register(new QuestsMechanic(e.getContainer().getManager(), e.getContainer().getFile(), e.getMechanicName(), e.getConfig()));
			break;
		}
		}
	}

	static Quest getQuestFromCurrent(Quester quester, String questName) {
		for (Map.Entry<Quest, Integer> questIntegerEntry : quester.getCurrentQuests().entrySet()) {
			Quest quest = questIntegerEntry.getKey();
			if ((quest.getName().toLowerCase().equals(questName))) {
				return quest;
			}
		}
		return null;
	}

}
