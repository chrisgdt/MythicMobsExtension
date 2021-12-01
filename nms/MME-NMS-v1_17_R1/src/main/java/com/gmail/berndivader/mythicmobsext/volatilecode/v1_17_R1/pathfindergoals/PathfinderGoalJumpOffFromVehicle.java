package com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;

public class PathfinderGoalJumpOffFromVehicle extends PathfinderGoal {
	protected EntityInsentient e;

	public PathfinderGoalJumpOffFromVehicle(EntityInsentient e2) {
		this.e = e2;
	}

	@Override
	public boolean a() {
		return e.getVehicle() != null;
	}

	@Override
	public boolean b() {
		if (e.getGoalTarget() != null)
			e.stopRiding();
		return false;
	}
}
