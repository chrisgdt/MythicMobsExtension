package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R1.pathfindergoals;

import net.minecraft.core.BlockPos;

import java.util.EnumSet;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;

public class PathfinderGoalFollowEntity extends Goal {
	private final Mob d;
	private final LivingEntity d1;
	private LivingEntity e;
	Level a;
	private final double f;
	private final PathNavigation g;
	private int h;
	float b;
	float c;
	private float i;

	public PathfinderGoalFollowEntity(Mob entity, LivingEntity entity1, double d0, float f, float f1) {
		this.d = entity;
		this.a = entity.level();
		this.d1 = entity1;
		this.f = d0;
		g = entity.getNavigation();
		c = f;
		b = f1;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		if ((!(entity.getNavigation() instanceof GroundPathNavigation))
				&& (!(entity.getNavigation() instanceof FlyingPathNavigation))) {
			throw new IllegalArgumentException("Unsupported mob type for FollowEntityGoal");
		}
	}

	@Override
	public boolean canUse() {
		this.e = this.d1;
		if ((this.e == null || !this.e.isAlive())
				|| (this.e instanceof Player) && this.e.isSpectator() || (d.distanceToSqr(this.e) < c * c))
			return false;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return (!g.isInProgress()) && (d.distanceToSqr(e) > b * b);
	}

	public void start() {
		h = 0;
		i = d.getPathfindingMalus(BlockPathTypes.WATER);
		d.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
	}

	public void stop() {
		e = null;
		g.getNodeEvaluator();
		d.setPathfindingMalus(BlockPathTypes.WATER, i);
	}

	public void tick() {
		d.getLookControl().setLookAt(e, 10.0F, d.getMaxHeadYRot());
		if (h-- <= 0) {
			h = 10;
			if ((!g.moveTo(e, f)) && (!d.isLeashed()) && (!d.isPassenger()) && (d.distanceToSqr(e) >= 144.0D)) {
				int i = Mth.floor(e.getX()) - 2;
				int j = Mth.floor(e.getZ()) - 2;
				int k = Mth.floor(e.getBoundingBox().maxY);
				for (int l = 0; l <= 4; l++) {
					for (int i1 = 0; i1 <= 4; i1++) {
						if (((l < 1) || (i1 < 1) || (l > 3) || (i1 > 3)) && (a(i, j, k, l, i1))) {
							CraftEntity entity = d.getBukkitEntity();
							Location to = new Location(entity.getWorld(), i + l + 0.5F, k, j + i1 + 0.5F, d.getYRot(),
									d.getXRot());
							EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
							Bukkit.getPluginManager().callEvent(event);
							if (event.isCancelled())
								return;
							to = event.getTo();
							d.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
							g.getNodeEvaluator();
							return;
						}
					}
				}
			}
		}
	}

	protected boolean a(int i, int j, int k, int l, int i1) {
		BlockPos blockposition = new BlockPos(i + l, k - 1, j + i1);
		BlockState iblockdata = a.getBlockState(blockposition);
		return (iblockdata.getDirectSignal(a, blockposition, Direction.DOWN) == 1) && (a.isEmptyBlock(blockposition.above()))
				&& (a.isEmptyBlock(blockposition.above(2)));
	}
}
