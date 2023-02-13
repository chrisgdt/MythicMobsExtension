package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R2.pathfindergoals;

import java.util.EnumSet;

import com.gmail.berndivader.mythicmobsext.Main;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class PathfinderGoalVexD extends Goal {

	Mob entity;
	BlockPos c;

	public PathfinderGoalVexD(Mob monster) {
		this.entity = monster;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (!entity.getMoveControl().hasWanted() && Main.random.nextInt(7) == 0)
			return true;
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void tick() {
		BlockPos blockposition = this.c;
		if (blockposition == null)
			blockposition = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
		int i2 = 0;
		while (i2 < 3) {
			BlockPos blockposition1 = blockposition.offset((double) Main.random.nextInt(15) - 7,
					(double) Main.random.nextInt(11) - 5, (double) Main.random.nextInt(15) - 7);
			if (entity.level.isEmptyBlock(blockposition1)) {
				entity.getMoveControl().setWantedPosition((double) blockposition1.getX() + 0.5, (double) blockposition1.getY() + 0.5,
						(double) blockposition1.getZ() + 0.5, 0.25);
				if (entity.getTarget() != null)
					break;
				entity.getLookControl().setLookAt((double) blockposition1.getX() + 0.5, (double) blockposition1.getY() + 0.5,
						(double) blockposition1.getZ() + 0.5, 180.0f, 20.0f);
				break;
			}
			++i2;
		}
	}
}
