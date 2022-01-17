package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.navigation;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.phys.Vec3D;

public class ControllerVex extends ControllerMove {
	public ControllerVex(EntityInsentient monster) {
		super(monster);
	}

	@Override
	public void a() {
		if (this.k == ControllerMove.Operation.b) { // MOVE_TO
			this.d.setNoGravity(true);
			double d0 = this.e - this.d.locX();
			double d1 = this.f - this.d.locY();
			double d2 = this.g - this.d.locZ();
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;
			if ((d3 = (double) MathHelper.c(d3)) < this.d.getBoundingBox().a()) {
				this.k = ControllerMove.Operation.a; // WAIT
				d.setMot(d.getMot().a(0.5));
			} else {
				Vec3D mot = d.getMot();
				mot.add(d0 / d3 * 0.05 * this.e, d1 / d3 * 0.05 * this.e, d2 / d3 * 0.05 * this.e);
				d.setMot(mot);
				if (d.getGoalTarget() == null) {
					d.setYRot((-(float) MathHelper.d(mot.getX(), mot.getZ())) * 57.295776f);
				} else {
					double d4 = d.getGoalTarget().locX() - d.locX();
					double d5 = d.getGoalTarget().locZ() - d.locZ();
					d.setYRot((-(float) MathHelper.d(d4, d5)) * 57.295776f);
				}
				d.ba = d.getYRot();
			}
		} else {
			this.d.setNoGravity(false);
			this.d.u(0.0f);
			this.d.v(0.0f);
		}
	}
}
