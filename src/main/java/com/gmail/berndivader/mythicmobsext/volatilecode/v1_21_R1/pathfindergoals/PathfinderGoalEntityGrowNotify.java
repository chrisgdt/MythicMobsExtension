package com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1.pathfindergoals;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.events.EntityGrownEvent;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.core.mobs.ActiveMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PathfinderGoalEntityGrowNotify extends Goal {
	protected Mob e;
	ActiveMob am;
	byte state;
	String signal;

	public PathfinderGoalEntityGrowNotify(Mob e2, String signal) {
		this.e = e2;
		this.signal = signal;
		this.am = Utils.mobmanager.getMythicMobInstance(e2.getBukkitEntity());
		this.state = e2.isBaby() ? (byte) 1 : (byte) -1;
	}

	@Override
	public boolean canUse() {
		return !e.isBaby() && state == 1;
	}

	@Override
	public boolean canContinueToUse() {
		if (state == 1) {
			state = 2;
			if (am != null && signal != null && !signal.isEmpty())
				am.signalMob(null, signal);
			Main.pluginmanager.callEvent(new EntityGrownEvent(e.getBukkitEntity(), am));
			return true;
		}
		return false;
	}
}
