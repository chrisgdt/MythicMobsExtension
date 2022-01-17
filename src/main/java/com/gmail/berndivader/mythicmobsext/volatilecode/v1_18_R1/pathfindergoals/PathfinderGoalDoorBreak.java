package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.pathfindergoals;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockDoor;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_18_R1.event.CraftEventFactory;

public class PathfinderGoalDoorBreak extends PathfinderGoalInteractDoor {
	int g;
	int h = -1;
	boolean flag;

	public PathfinderGoalDoorBreak(EntityInsentient e, boolean bl1) {
		super(e, bl1);
	}

	public boolean a() {
		if (!super.a())
			return false;
		if (bl1 && !this.a.getWorld().getGameRules().getBoolean(GameRules.c)) // MOB_GRIEFING
			return false;
		return !this.g();
	}

	@Override
	public void c() {
		super.c();
		this.g = 0;
	}

	@Override
	public boolean b() {
		double d0 = this.a.e(new Vec3D(this.b.getX(), this.b.getY(), this.b.getZ()));
		if (this.g <= 240 && !this.g() && d0 < 4.0) {
			flag = true;
			return flag;
		}
		flag = false;
		return flag;
	}

	@Override
	public void d() {
		super.d();
		this.a.getWorld().b(this.a.getId(), this.b, -1);
	}

	@Override
	public void e() {
		super.e();
		if (this.a.getRandom().nextInt(20) == 0)
			this.a.getWorld().triggerEffect(1019, this.b, 0);
		this.g++;
		int i2 = (int) ((float) this.g / 240.0f * 10.0f);
		if (i2 != this.h) {
			this.a.getWorld().b(this.a.getId(), this.b, i2);
			this.h = i2;
		}
		if (this.g == 240) {
			if (CraftEventFactory.callEntityBreakDoorEvent(this.a, this.b).isCancelled()) {
				this.c();
				return;
			}
			this.a.getWorld().a(this.b, false);
			this.a.getWorld().triggerEffect(1021, this.b, 0);
			this.a.getWorld().triggerEffect(2001, this.b, Block.getCombinedId(this.a.getWorld().getType(this.b)));
		}
	}

	protected boolean g() {
		if (!this.d)
			return false;
		IBlockData iblockdata = this.a.getWorld().getType(this.b);
		if (!(iblockdata.getBlock() instanceof BlockDoor)) {
			this.d = false;
			return false;
		}
		return (Boolean) iblockdata.get(BlockDoor.b); // OPEN
	}
}
