package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R3.navigation;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class NavigationClimb extends GroundPathNavigation {
	private BlockPos i;

	public NavigationClimb(Mob mob, Level level) {
		super(mob, level);
		NMSUtils.setField("moveController", Mob.class, this.mob, new MoveController(this.mob));
	}

	@Override
	public Path createPath(BlockPos blockPosition, int i1) {
		this.i = blockPosition;
		return super.createPath(blockPosition, i1);
	}

	@Override
	public Path createPath(Entity entity, int i1) {
		this.i = new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
		return super.createPath(entity, i1);
	}

	@Override
	public boolean moveTo(Entity entity, double d2) {
		Path pathEntity = this.createPath(entity, 0);
		if (pathEntity != null) {
			return this.moveTo(pathEntity, d2);
		}
		this.i = new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
		this.speedModifier = d2;
		return true;
	}

	@Override
	public void tick() {
		if (this.isInProgress()) {
			if (this.i != null) {
				if (this.i.closerToCenterThan(this.mob.position(), this.mob.getBbWidth())
						|| this.mob.getY() > (double) this.i.getY()
								&& new BlockPos(this.i.getX(), this.mob.getBlockY(), this.i.getZ())
										.closerToCenterThan(this.mob.position(), this.mob.getBbWidth())) {
					this.i = null;
				} else {
					if (!(this.mob.getMoveControl() instanceof MoveController)) {
						NMSUtils.setField("moveController", Mob.class, this.mob, new MoveController(this.mob));
					}
					((MoveController) this.mob.getMoveControl()).aa(this.i.getX(), this.i.getY(), this.i.getZ(),
							this.speedModifier);
				}
			}
			return;
		}
		super.tick();
	}

	static class MoveController extends MoveControl {

		public MoveController(Mob arg0) {
			super(arg0);
		}

		public void aa(double d2, double d3, double d4, double d5) {
			this.wantedX = d2;
			this.wantedY = d3;
			this.wantedZ = d4;
			this.speedModifier = d5;
			this.operation = Operation.MOVE_TO;
		}
	}
}
