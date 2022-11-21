package com.gmail.berndivader.mythicmobsext.javascript;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.ScriptException;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

public class NashornMythicMobsEvents implements Listener {

	public NashornMythicMobsEvents() {
		Main.pluginmanager.registerEvents(this, Main.getPlugin());
	}

	@EventHandler
	public void onMythicMobsReload(MythicReloadedEvent e) {
		try {
			Nashorn.scripts = new String(Files.readAllBytes(Paths.get(Utils.str_PLUGINPATH, Nashorn.filename)));
			if(Nashorn.engine!=null) {
				Nashorn.engine.eval(Nashorn.scripts);
				Main.logger.info("Javascripts reloaded.");
			}else {
				Main.logger.warning("No javascriptengine present!");
			}
		} catch (IOException | ScriptException ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler
	public void onMechanicLoadEvent(MythicMechanicLoadEvent e) {
		String s1 = e.getMechanicName().toLowerCase();
		switch (s1) {
		case "jsmechanic":
		case "jsmechanic_ext": {
			e.register(new JavascriptMechanic(e.getContainer().getManager(), e.getContainer().getFile(), e.getConfig().getLine(), e.getConfig()));
			//e.register(new JavascriptMechanic(e.getContainer().getManager(), e.getContainer().getConfigLine(), e.getConfig()));
			break;
		}
		case "math":
		case "math_ext": {
			e.register(new EvalMechanic(e.getContainer().getManager(), e.getContainer().getFile(), e.getConfig().getLine(), e.getConfig()));
			//e.register(new EvalMechanic(e.getContainer().getManager(), e.getContainer().getConfigLine(), e.getConfig()));
			break;
		}
		}
	}

	@EventHandler
	public void onConditionLoadEvent(MythicConditionLoadEvent e) {
		switch (e.getConditionName().toLowerCase()) {
		case "jscondition":
		case "jscondition_ext": {
			e.register(new JavascriptCondition(e.getConfig().getLine(), e.getConfig()));
		}
		}
	}

}
