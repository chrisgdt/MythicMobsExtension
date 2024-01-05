package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R3.navigation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class ControllerVex extends MoveControl {
	public ControllerVex(Mob monster) {
		super(monster);
	}

	@Override
	public void tick() {
		if (this.operation == MoveControl.Operation.MOVE_TO) {
			this.mob.setNoGravity(true);
			double d0 = this.getWantedX() - this.mob.getX();
			double d1 = this.getWantedY() - this.mob.getY();
			double d2 = this.getWantedZ() - this.mob.getZ();
			float d3 = (float) (d0 * d0 + d1 * d1 + d2 * d2);
			if ((d3 = Mth.sqrt(d3)) < this.mob.getBoundingBox().getSize()) {
				this.operation = MoveControl.Operation.WAIT;
				this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5));
			} else {
				Vec3 mot = this.mob.getDeltaMovement();
				mot.add(d0 / d3 * 0.05 * this.getSpeedModifier(), d1 / d3 * 0.05 * this.getSpeedModifier(), d2 / d3 * 0.05 * this.getSpeedModifier());
				this.mob.setDeltaMovement(mot);
				if (this.mob.getTarget() == null) {
					// TODO : eyeHeight is in private
					//this.mob.aD =
					this.mob.yHeadRot = (-(float) Mth.atan2(mot.x, mot.z)) * 57.295776f;
				} else {
					double d4 = this.mob.getTarget().getX() - this.mob.getX();
					double d5 = this.mob.getTarget().getZ() - this.mob.getZ();
					//this.mob.aD =
					this.mob.yHeadRot = (-(float) Mth.atan2(d4, d5)) * 57.295776f;
				}
			}
		} else {
			this.mob.setNoGravity(false);
			this.mob.setYya(0.0f);
			this.mob.setXxa(0.0f);
		}
	}
}
