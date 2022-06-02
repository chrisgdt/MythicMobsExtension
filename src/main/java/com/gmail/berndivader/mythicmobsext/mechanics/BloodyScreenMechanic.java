package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "bloodyscreen,bloodyscreen_ext", author = "BerndiVader")
public class BloodyScreenMechanic extends SkillMechanic implements ITargetedEntitySkill {
	static String str = "mme_bloodyscreen";
	boolean bl1, max_alpha;
	int timer;

	public BloodyScreenMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		bl1 = mlc.getBoolean("play", true);
		timer = mlc.getInteger("timer", -1);
		max_alpha = false;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity var2) {
		if (var2.isPlayer()) {
			final Player player = (Player) var2.getBukkitEntity();
			if (timer == -1) {
				if (!this.bl1)
					player.removeMetadata(str, Main.getPlugin());
				Volatile.handler.setWorldborder((Player) var2.getBukkitEntity(), 0, this.bl1);
			} else {
				player.setMetadata(str, new FixedMetadataValue(Main.getPlugin(), true));
				Volatile.handler.setWorldborder((Player) var2.getBukkitEntity(), 0, true);
				new BukkitRunnable() {
					@Override
					public void run() {
						if (player.isOnline()) {
							if (player.hasMetadata(str))
								player.removeMetadata(str, Main.getPlugin());
							Volatile.handler.setWorldborder((Player) var2.getBukkitEntity(), 0, false);
						}
					}
				}.runTaskLater(Main.getPlugin(), timer);
			}
		}
		return SkillResult.SUCCESS;
	}

}
