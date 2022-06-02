package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "customvelocity", author = "BerndiVader")
public class CustomVelocityMechanic extends SkillMechanic implements ITargetedEntitySkill {
	protected double velocityX;
	protected double velocityY;
	protected double velocityZ;
	protected boolean debug;
	private char c;

	public CustomVelocityMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.debug = mlc.getBoolean("debug", false);
		this.velocityX = mlc.getDouble(new String[] { "velocityx", "vx", "x" }, 0.0D);
		this.velocityY = mlc.getDouble(new String[] { "velocityy", "vy", "y" }, 0.0D);
		this.velocityZ = mlc.getDouble(new String[] { "velocityz", "vz", "z" }, 0.0D);
		this.c = mlc.getString(new String[] { "mode", "m" }, "SET").toUpperCase().charAt(0);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		Entity e = target.getBukkitEntity();
		Vector v = e.getVelocity().clone();
		Vector vb = v.clone();
		switch (c) {
			case 'A':
				v.setX(v.getX() + this.velocityX);
				v.setY(v.getY() + this.velocityY);
				v.setZ(v.getZ() + this.velocityZ);
				break;
			case 'M':
				v.setX(v.getX() * this.velocityX);
				v.setY(v.getY() * this.velocityY);
				v.setZ(v.getZ() * this.velocityZ);
				break;
			default:
				v = new Vector(this.velocityX, this.velocityY, this.velocityZ);
				break;
		}
		if (debug)
			System.out.println("dx:" + v.getX() + " dy:" + v.getY() + " dz:" + v.getZ() + " len:" + v.length());
		if (Double.isNaN(v.length()) || Double.isInfinite(v.length()))
			v = vb;
		e.setVelocity(v);
		return SkillResult.SUCCESS;
	}
}
