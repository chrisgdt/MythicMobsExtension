package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R3.pathfindergoals;

import com.gmail.berndivader.mythicmobsext.Main;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class PathfinderGoalInteractDoor extends Goal {
	protected Mob mob;
	protected BlockPos b = BlockPos.ZERO;
	protected DoorBlock c;
	boolean d, bl1;
	float e, f;
	double dx, dy, dz;

	public PathfinderGoalInteractDoor(Mob e, boolean bl1) {
		this.mob = e;
		this.bl1 = bl1;
		if (!(e.getNavigation() instanceof GroundPathNavigation))
			Main.logger.warning("No navigation mob");
		((GroundPathNavigation) e.getNavigation()).setCanOpenDoors(true);
	}

	@Override
	public boolean canUse() {
		if (!this.mob.horizontalCollision || (bl1 && !this.mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)))
			return false;
		GroundPathNavigation n1 = (GroundPathNavigation) this.mob.getNavigation();
		Path pe1 = n1.getPath();
		if (pe1 == null || pe1.notStarted() || !n1.canOpenDoors())
			return false;
		for (int i1 = 0; i1 < Math.min(pe1.getNextNodeIndex() + 2, pe1.getNodeCount()); i1++) {
			Node pp1 = pe1.getNode(i1);
			this.b = new BlockPos(pp1.x, pp1.y + 1, pp1.z);
			if (this.mob.distanceToSqr(this.b.getX(), this.mob.getY(), this.b.getZ()) > 2.25)
				continue;
			this.c = this.a(this.b);
			if (this.c == null)
				continue;
			return true;
		}
		this.b = new BlockPos(this.mob.getBlockX(), this.mob.getBlockY(), this.mob.getBlockZ()).above();
		this.c = this.a(this.b);
		return this.c != null;
	}

	@Override
	public boolean canContinueToUse() {
		return !this.d;
	}

	@Override
	public void start() {
		this.d = false;
		this.e = (float) ((double) ((float) this.b.getX() + 0.5f) - this.mob.getX());
		this.f = (float) ((double) ((float) this.b.getZ() + 0.5f) - this.mob.getZ());
	}

	@Override
	public void tick() {
		float f3 = (float) ((double) ((float) this.b.getX() + 0.5f) - this.mob.getX());
		float f4 = this.e * f3 + this.f * ((float) ((double) ((float) this.b.getZ() + 0.5f) - this.mob.getZ()));
		if (f4 < 0.0f)
			this.d = true;
	}

	private DoorBlock a(BlockPos bp1) {
		BlockState bd1 = this.mob.level().getBlockState(bp1);
		Block b1 = bd1.getBlock();
		if ((b1 instanceof DoorBlock) && ((DoorBlock) b1).type().canOpenByHand())
			return (DoorBlock) b1;
		return null;
	}
}
