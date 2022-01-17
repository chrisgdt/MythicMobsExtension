package com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals;

import net.minecraft.world.entity.EntityInsentient;

public class PathfinderGoalDoorOpen extends PathfinderGoalInteractDoor {
	boolean g;
	int h;

	public PathfinderGoalDoorOpen(EntityInsentient e, boolean bl1) {
		super(e, bl1);
		this.a = e;
		this.g = true;
	}

	@Override
	public boolean b() {
		return this.g && this.h > 0 && super.b();
	}

	@Override
	public void c() {
		this.h = 20;
		this.c.setDoor(null, this.a.getWorld(), null, this.b, true);
	}

	@Override
	public void d() {
		if (this.g)
			this.c.setDoor(null, this.a.getWorld(), null, this.b, false);
	}

	@Override
	public void e() {
		this.h--;
		super.e();
	}
}
