package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.navigation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;

public class ControllerFly extends MoveControl {
	public ControllerFly(Mob entityInsentient) {
		super(entityInsentient);
	}

	@Override
	public void tick() {
		if (this.operation == Operation.STRAFE) {
			NodeEvaluator pathfinderAbstract;
			float f2 = (float) this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
			float f3 = (float) this.speedModifier * f2;
			float f4 = this.strafeForwards;
			float f5 = this.strafeRight;
			float f6 = Mth.sqrt(f4 * f4 + f5 * f5);
			if (f6 < 1.0f)
				f6 = 1.0f;
			f6 = f3 / f6;
			float f7 = Mth.sin(this.mob.getYRot() * 0.017453292f);
			float f8 = Mth.cos(this.mob.getYRot() * 0.017453292f);
			float f9 = (f4 *= f6) * f8 - (f5 *= f6) * f7;
			float f10 = f5 * f8 + f4 * f7;
			PathNavigation navigationAbstract = this.mob.getNavigation();
			if (navigationAbstract != null && (pathfinderAbstract = navigationAbstract.getNodeEvaluator()) != null
					&& pathfinderAbstract.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double) f9),
					Mth.floor(this.mob.getY()),
					Mth.floor(this.mob.getZ() + (double) f10)) != BlockPathTypes.WALKABLE) {
				this.strafeForwards = 1.0f;
				this.strafeRight = 0.0f;
				f3 = f2;
			}
			this.mob.setYBodyRot(f3);
			this.mob.setXxa(this.strafeForwards);
			this.mob.setYya(this.strafeRight);
			this.operation = Operation.WAIT;
		} else if (this.operation == MoveControl.Operation.MOVE_TO) {
			this.operation = MoveControl.Operation.WAIT;
			this.mob.setNoGravity(true);
			double d2 = this.wantedX - this.mob.getX();
			double d3 = this.wantedY - this.mob.getY();
			double d4 = this.wantedZ - this.mob.getZ();
			double d5 = d2 * d2 + d3 * d3 + d4 * d4;
			if (d5 < 2.500000277905201E-7) {
				this.mob.setYya(0.0f);
				this.mob.setXxa(0.0f);
				return;
			}
			float f2 = (float) (Mth.atan2(d4, d2) * 57.2957763671875) - 90.0f;
			this.mob.yHeadRot = this.rotlerp(this.mob.getYRot(), f2, 10.0f);
			float f3 = (float) (this.speedModifier * this.mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
			this.mob.setYBodyRot(f3);
			double d6 = Mth.lfloor(d2 * d2 + d4 * d4);
			float f4 = (float) (-Mth.atan2(d3, d6) * 57.2957763671875);
			this.mob.setXRot(this.rotlerp(this.mob.getYRot(), f4, 10.0f));
			this.mob.setYya(d3 > 0.0 ? f3 : -f3);
		} else {
			this.mob.setNoGravity(false);
			this.mob.setYya(0.0f);
			this.mob.setXxa(0.0f);
		}
	}
}
