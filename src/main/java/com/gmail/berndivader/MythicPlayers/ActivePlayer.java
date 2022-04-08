package com.gmail.berndivader.MythicPlayers;

import java.util.Optional;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.bukkit.entity.Player;

public class ActivePlayer extends ActiveMob {
	protected MobExecutor mobmanager = Utils.mobmanager;

	public ActivePlayer(AbstractEntity e, MythicMob type, int level) {
		super(e, type, level);
	}

	public Optional<ActiveMob> getActiveMob() {
		return mobmanager.getActiveMob(this.getUniqueId());
	}

	public Player getPlayer() {
		return BukkitAdapter.adapt(this.getEntity().asPlayer());
	}

}
