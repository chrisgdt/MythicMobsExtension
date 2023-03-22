package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R3.pathfindergoals;

import net.minecraft.world.entity.Mob;

public class PathfinderGoalDoorOpen extends PathfinderGoalInteractDoor {
	boolean g;
	int h;

	public PathfinderGoalDoorOpen(Mob e, boolean bl1) {
		super(e, bl1);
		this.mob = e;
		this.g = true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.g && this.h > 0 && super.canContinueToUse();
	}

	@Override
	public void start() {
		this.h = 20;
		this.c.setOpen(this.mob, this.mob.level, this.c.defaultBlockState(), this.b, true);
	}

	@Override
	public void stop() {
		if (this.g)
			this.c.setOpen(this.mob, this.mob.level, this.c.defaultBlockState(), this.b, false);
	}

	@Override
	public void tick() {
		this.h--;
		super.tick();
	}
}
