package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_19_R1.event.CraftEventFactory;

public class PathfinderGoalDoorBreak extends PathfinderGoalInteractDoor {
	int g;
	int h = -1;
	boolean flag;

	public PathfinderGoalDoorBreak(Mob e, boolean bl1) {
		super(e, bl1);
	}

	@Override
	public boolean canUse() {
		if (!super.canUse())
			return false;
		if (bl1 && !this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
			return false;
		return !this.g();
	}

	@Override
	public void start() {
		super.start();
		this.g = 0;
	}

	@Override
	public boolean canContinueToUse() {
		double d0 = this.mob.distanceToSqr(new Vec3(this.b.getX(), this.b.getY(), this.b.getZ()));
		if (this.g <= 240 && !this.g() && d0 < 4.0) {
			flag = true;
			return flag;
		}
		flag = false;
		return flag;
	}

	@Override
	public void stop() {
		super.stop();
		this.mob.level.globalLevelEvent(this.mob.getId(), this.b, -1);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.mob.getRandom().nextInt(20) == 0)
			this.mob.level.destroyBlockProgress(1019, this.b, 0);
		this.g++;
		int i2 = (int) ((float) this.g / 240.0f * 10.0f);
		if (i2 != this.h) {
			this.mob.level.globalLevelEvent(this.mob.getId(), this.b, i2);
			this.h = i2;
		}
		if (this.g == 240) {
			if (CraftEventFactory.callEntityBreakDoorEvent(this.mob, this.b).isCancelled()) {
				this.tick();
				return;
			}
			this.mob.level.removeBlock(this.b, false);

			this.mob.level.destroyBlockProgress(1021, this.b, 0);
			this.mob.level.destroyBlockProgress(2001, this.b, Block.getId(this.mob.level.getBlockState(this.b)));
		}
	}

	protected boolean g() {
		if (!this.d)
			return false;
		BlockState iblockdata = this.mob.getLevel().getBlockState(this.b);
		if (!(iblockdata.getBlock() instanceof DoorBlock)) {
			this.d = false;
			return false;
		}
		return iblockdata.getValue(DoorBlock.OPEN);
	}
}
