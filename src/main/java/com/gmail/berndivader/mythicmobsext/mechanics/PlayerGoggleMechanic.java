package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Vec2D;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Handler;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import java.io.File;

@ExternalAnnotation(name = "playergoggle,playergoggleat", author = "BerndiVader")
public class PlayerGoggleMechanic extends SkillMechanic implements ITargetedEntitySkill {
	public static String str = "mmGoggle";
	private long dur;
	private Handler vh = Volatile.handler;

	public PlayerGoggleMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.dur = mlc.getInteger(new String[] { "duration", "dur" }, 120);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!target.isPlayer() || target.getBukkitEntity().hasMetadata(str))
			target.getBukkitEntity().removeMetadata(str, Main.getPlugin());
		target.getBukkitEntity().setMetadata(str, new FixedMetadataValue(Main.getPlugin(), true));
		final Player p = (Player) target.getBukkitEntity();
		final AbstractEntity caster = data.getCaster().getEntity();
		final long d = this.dur;
		new BukkitRunnable() {
			long count = 0;

			@Override
			public void run() {
				if (p == null || p.isDead() || count > d || !p.hasMetadata(str)) {
					p.removeMetadata(str, Main.getPlugin());
					this.cancel();
				} else {
					Vec2D v = MathUtils.lookAtVec(p.getEyeLocation(),
							caster.getBukkitEntity().getLocation().add(0, caster.getEyeHeight(), 0));
					PlayerGoggleMechanic.this.vh.playerConnectionLookAt(p, (float) v.getX(), (float) v.getY());
				}
				count++;
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 1L, 1L);
		return SkillResult.SUCCESS;
	}

}
