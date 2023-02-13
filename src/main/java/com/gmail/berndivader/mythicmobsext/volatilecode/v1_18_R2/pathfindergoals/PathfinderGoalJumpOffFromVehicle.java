package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R2.pathfindergoals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PathfinderGoalJumpOffFromVehicle extends Goal {
	protected Mob e;

	public PathfinderGoalJumpOffFromVehicle(Mob e2) {
		this.e = e2;
	}

	@Override
	public boolean canUse() {
		return e.getVehicle() != null;
	}

	@Override
	public boolean canContinueToUse() {
		if (e.getTarget() != null)
			e.stopRiding();
		return false;
	}
}
