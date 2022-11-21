package com.gmail.berndivader.mythicmobsext.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Matcher;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.entities.BukkitWolf;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.*;
import io.lumine.mythic.core.utils.Patterns;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.compatibility.nocheatplus.NoCheatPlusSupport;
import com.gmail.berndivader.mythicmobsext.compatibility.papi.Papi;
import com.gmail.berndivader.mythicmobsext.compatibilitylib.BukkitSerialization;
import com.gmail.berndivader.mythicmobsext.config.Config;
import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.mechanics.NoDamageTicksMechanic;
import com.gmail.berndivader.mythicmobsext.mechanics.PlayerGoggleMechanic;
import com.gmail.berndivader.mythicmobsext.mechanics.PlayerSpinMechanic;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class Utils implements Listener {
	public static MythicBukkit mythicmobs;
	public static MobExecutor mobmanager;
	public static int serverV;
	public static int renderLength;
	public static HashMap<UUID, Vec3D> players;
	public static final String SERIALIZED_ITEM = "_b64i";
	public static final String signal_AISHOOT = "AISHOOT";
	public static final String signal_AIHIT = "AIHIT";
	public static final String signal_CHUNKUNLOAD = "CHUNKUNLOAD";
	public static final String meta_WALKSPEED = "MMEXTWALKSPEED";
	public static final String mpNameVar = "mythicprojectile";
	public static final String noTargetVar = "nottargetable";
	public static final String meta_BOWTICKSTART = "mmibowtick";
	public static final String meta_BOWTENSIONLAST = "mmibowtensionlast";
	public static final String meta_MYTHICDAMAGE = "MythicDamage";
	public static final String meta_DAMAGECAUSE = "DamageCause";
	public static final String meta_LASTDAMAGER = "LastDamager";
	public static final String meta_LASTDAMAGECAUSE = "LastDamageCause";
	public static final String meta_LASTDAMAGEAMOUNT = "LastDamageAmount";
	public static final String meta_MMRPGITEMDMG = "mmrpgitemdmg";
	public static final String meta_MMEDIGGING = "MMEDIGGING";
	public static final String meta_LASTCOLLIDETYPE = "MMELASTCOLLIDE";
	public static final String meta_NCP = "NCP";
	public static final String meta_SPAWNREASON = "SPAWNREASON";
	public static final String meta_CUSTOMSPAWNREASON = "SETSPAWNREASON";
	public static final String meta_RESOURCEPACKSTATUS = "MMERESPACKSTAT";
	public static final String meta_NOSUNBURN = "MMENOSUN";
	public static final String meta_SLOTCHANGEDSTAMP = "SLOTSTAMP";
	public static final String meta_BACKBACKTAG = "BAG_POS_TAG";
	public static final String meta_TRAVELPOINTS = "MME_TRAVEL_POINTS";
	public static final String meta_INVCLICKOLDCURSOR = "mmeinvclickold";
	public static final String meta_INVCLICKNEWCURSOR = "mmeinvclicknew";
	public static final String signal_GOAL_TRAVELEND = "GOAL_TRAVELEND";
	public static final String signal_GOAL_TRAVELPOINT = "GOAL_TRAVELPOINT";
	public static final String meta_DISORIENTATION = "MMEDISORIENTATION";
	public static final String meta_CLICKEDSKILL = "click_skill";
	public static final String meta_LASTCLICKEDSLOT = "lastclickedslot";
	public static final String signal_BACKBAGCLICK = "BAGCLICKED";
	public static final String meta_LASTCLICKEDBAG = "lastclickedbag";
	public static final String meta_LASTHEALAMOUNT = "mmelastheal";
	public static final String meta_STUNNED = "mmeStunned";
	public static String scripts;
	public static String str_PLUGINPATH;
	public static HashSet<Advancement> advancements;
	static Field threattable_field;
	public static Field action_var_field;

	static boolean papi_ispresent;
	
	static MetaRunner metaRunner;

	static {
		mythicmobs = MythicBukkit.inst();
		mobmanager = mythicmobs.getMobManager();
		renderLength = 512;
		str_PLUGINPATH = Main.getPlugin().getDataFolder().toString();
		try {
			serverV = Integer
					.parseInt(Bukkit.getServer().getClass().getPackage().getName().substring(23).split("_")[1]);
		} catch (Exception e) {
			serverV = 11;
		}
		if (Utils.serverV > 11) {
			advancements = new HashSet<>();
			for (Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext();) {
				Advancement adv = iter.next();
				advancements.add(adv);
			}
		}
		players = new HashMap<>();
		try {
			threattable_field = ActiveMob.ThreatTable.class.getDeclaredField("threatTable");
			threattable_field.setAccessible(true);
			action_var_field = SkillCondition.class.getDeclaredField("actionVar");
			action_var_field.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		papi_ispresent = Main.pluginmanager.getPlugin(Papi.str_PLUGINNAME) != null;
	}

	public Utils() {
		Main.pluginmanager.registerEvents(new UndoBlockListener(), Main.getPlugin());
		Main.getPlugin().getServer().getPluginManager().registerEvents(this, Main.getPlugin());
		
		metaRunner=new MetaRunner();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerClickInventory(InventoryClickEvent e) {
		ItemStack new_cursor = e.getCurrentItem();
		ItemStack old_cursor = e.getCursor();
		store_clicked_items(e.getWhoClicked(), new_cursor, old_cursor);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerDraggingItem(InventoryDragEvent e) {
		ItemStack new_cursor = e.getCursor();
		ItemStack old_cursor = e.getOldCursor();
		store_clicked_items(e.getWhoClicked(), new_cursor, old_cursor);
	}

	static void store_clicked_items(Entity clicker, ItemStack new_cursor, ItemStack old_cursor) {
		if (new_cursor == null)
			new_cursor = new ItemStack(Material.AIR);
		if (old_cursor == null)
			old_cursor = new ItemStack(Material.AIR);

		clicker.setMetadata(meta_INVCLICKNEWCURSOR, new FixedMetadataValue(Main.getPlugin(),
				SERIALIZED_ITEM + BukkitSerialization.itemStackToBase64(new_cursor)));
		clicker.setMetadata(meta_INVCLICKOLDCURSOR, new FixedMetadataValue(Main.getPlugin(),
				SERIALIZED_ITEM + BukkitSerialization.itemStackToBase64(old_cursor)));
	}

	@EventHandler
	public void playerHeldItem(PlayerItemHeldEvent e) {
		e.getPlayer().setMetadata(meta_SLOTCHANGEDSTAMP,
				new FixedMetadataValue(Main.getPlugin(), System.currentTimeMillis()));
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent e) {
		if (e.getChunk() == null)
			return;
		for (Entity entity : e.getChunk().getEntities()) {
			if (mobmanager.isActiveMob(entity.getUniqueId())) {
				mobmanager.getMythicMobInstance(entity).signalMob(BukkitAdapter.adapt(entity), signal_CHUNKUNLOAD);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void testDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity().hasMetadata(("mme_dmg_chk")))
			e.setCancelled(true);
		e.getEntity().removeMetadata("mme_dmg_chk", Main.getPlugin());
		e.getEntity().setMetadata("mme_dmg_amount", new FixedMetadataValue(Main.getPlugin(), e.getFinalDamage()));
	}

	@EventHandler
	public void creeperExplode(EntityExplodeEvent e) {
		if (e.isCancelled() || (e.getEntityType() != EntityType.CREEPER))
			return;
		if (mobmanager.isActiveMob(e.getEntity().getUniqueId())) {
			ActiveMob am = mobmanager.getMythicMobInstance(e.getEntity());
			if (am.getType().getConfig().getBoolean("BlocksOnFire", false)) {
				int amount = am.getType().getConfig().getInt("BlocksOnFireAmount", 10);
				World w = e.getLocation().getWorld();
				Location l = e.getLocation();
				for (int i = 0; i < amount; i++) {
					FallingBlock fb = w.spawnFallingBlock(l, Material.FIRE.createBlockData());
					fb.setVelocity(new Vector(UndoBlockListener.getRandomVel(-0.5, 0.5),
							UndoBlockListener.getRandomVel(0.3, 0.8), UndoBlockListener.getRandomVel(-0.5, 0.5)));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void tagAndChangeSpawnReason(CreatureSpawnEvent e) {
		if (e.isCancelled())
			return;
		e.getEntity().setMetadata(meta_SPAWNREASON, new FixedMetadataValue(Main.getPlugin(), e.getSpawnReason()));
	}

	@EventHandler
	public void storeBowTensionEvent(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (p.getInventory().getItemInMainHand().getType() == Material.BOW
				|| p.getInventory().getItemInOffHand().getType() == Material.BOW) {
			p.setMetadata(meta_BOWTICKSTART,
					new FixedMetadataValue(Main.getPlugin(), NMSUtils.getCurrentTick(Bukkit.getServer())));
			new BukkitRunnable() {
				float f1;

				@Override
				public void run() {
					if (p != null && p.isOnline() && (f1 = getBowTension(p)) > -1) {
						p.setMetadata(meta_BOWTENSIONLAST, new FixedMetadataValue(Main.getPlugin(), f1));
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(Main.getPlugin(), 0l, 0l);
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if (e.isCancelled() || !(e.getEntity().getShooter() instanceof Entity))
			return;
		final Entity s = (Entity) e.getEntity().getShooter();
		final ActiveMob am = mobmanager.getMythicMobInstance(s);
		if (am != null) {
			TriggeredSkill ts = new TriggeredSkill(SkillTriggers.SHOOT, am, am.getEntity().getTarget(), true);
			e.setCancelled(ts.getCancelled());
		}
	}

	@EventHandler
	public void RemoveFallingBlockProjectile(EntityChangeBlockEvent e) {
		if (e.getEntity().hasMetadata(Utils.mpNameVar)) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void mmTriggerOnKill(EntityDeathEvent e) {
		EntityDamageEvent entityDamageEvent = e.getEntity().getLastDamageCause();
		if (entityDamageEvent != null && !entityDamageEvent.isCancelled()
				&& entityDamageEvent instanceof EntityDamageByEntityEvent) {
			LivingEntity damager = getAttacker(((EntityDamageByEntityEvent) entityDamageEvent).getDamager());
			if (damager != null && mobmanager.isActiveMob(damager.getUniqueId())) {
				new TriggeredSkill(SkillTriggers.KILL, mobmanager.getMythicMobInstance(damager),
						BukkitAdapter.adapt(e.getEntity()), true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMythicCustomRPGItemDamage(EntityDamageByEntityEvent e) {
		LivingEntity victim = null;
		if (e.getEntity() instanceof LivingEntity)
			victim = (LivingEntity) e.getEntity();
		if (victim == null || !victim.hasMetadata(meta_MYTHICDAMAGE))
			return;
		if (victim.getMetadata(meta_MMRPGITEMDMG).get(0).asBoolean()) {
			victim.removeMetadata(meta_MYTHICDAMAGE, Main.getPlugin());
			onEntityDamageTaken(e, victim);
		}
	}

	@EventHandler
	public void onMythicCustomDamage(EntityDamageByEntityEvent e) {
		LivingEntity victim = null;
		if (e.getEntity() instanceof LivingEntity)
			victim = (LivingEntity) e.getEntity();
		if (victim == null || !victim.hasMetadata(meta_MYTHICDAMAGE))
			return;
		if (!victim.getMetadata(meta_MMRPGITEMDMG).get(0).asBoolean()) {
			victim.removeMetadata(meta_MYTHICDAMAGE, Main.getPlugin());
			onEntityDamageTaken(e, victim);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void storeDamageCause(EntityDamageEvent e) {
		if (e.isCancelled())
			return;
		Entity victim = e.getEntity();
		if (victim != null && victim.hasMetadata(meta_MYTHICDAMAGE) && victim.hasMetadata(meta_DAMAGECAUSE)) {
			NMSUtils.setFinalField("cause", EntityDamageEvent.class, e,
					DamageCause.valueOf(victim.getMetadata(meta_DAMAGECAUSE).get(0).asString()));
			victim.removeMetadata(meta_DAMAGECAUSE, Main.getPlugin());
		}
		DamageCause cause = e.getCause();
		if (e instanceof EntityDamageByEntityEvent) {
			Entity damager = Utils.getAttacker(((EntityDamageByEntityEvent) e).getDamager());
			if (damager != null)
				victim.setMetadata(meta_LASTDAMAGER,
						new FixedMetadataValue(Main.getPlugin(), damager.getType().toString()));
		} else if (victim.hasMetadata(meta_LASTDAMAGER)) {
			victim.removeMetadata(meta_LASTDAMAGER, Main.getPlugin());
		}
		victim.setMetadata(meta_LASTDAMAGECAUSE, new FixedMetadataValue(Main.getPlugin(), cause.toString()));
		victim.setMetadata(meta_LASTDAMAGEAMOUNT, new FixedMetadataValue(Main.getPlugin(), e.getDamage()));
	}

	@EventHandler
	public void triggerDamageForNoneEntity(EntityDamageEvent e) {
		TriggeredSkill ts;
		final Entity victim = e.getEntity();
		if (e instanceof EntityDamageByEntityEvent || !(victim instanceof LivingEntity) || victim instanceof Player)
				//|| mobmanager.getVoidList().contains(victim.getUniqueId()))
			return;
		ActiveMob am = mobmanager.getMythicMobInstance(victim);
		if (am == null || !am.getType().getConfig().getBoolean("onDamageForOtherCause"))
			return;
		ts = new TriggeredSkill(SkillTriggers.DAMAGED, am, null, true);
		if (ts.getCancelled())
			e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		UUID uuid;
		if (players.containsKey(uuid = p.getUniqueId()))
			players.remove(uuid);
		if (p.hasMetadata(NoDamageTicksMechanic.str))
			e.getPlayer().removeMetadata(NoDamageTicksMechanic.str, Main.getPlugin());
		if (p.hasMetadata(meta_STUNNED)) {
			p.setGravity(true);
			p.removeMetadata(meta_STUNNED, Main.getPlugin());
		}
		if (p.hasMetadata(PlayerSpinMechanic.str)) {
			p.removeMetadata(PlayerSpinMechanic.str, Main.getPlugin());
		}
		if (p.hasMetadata(PlayerGoggleMechanic.str)) {
			p.removeMetadata(PlayerGoggleMechanic.str, Main.getPlugin());
		}
	}

	private static void onEntityDamageTaken(EntityDamageByEntityEvent e, LivingEntity victim) {
		boolean debug = victim.getMetadata("mmcdDebug").get(0).asBoolean();
		if (debug)
			Main.logger.info("CustomDamageMechanic cancelled? " + Boolean.toString(e.isCancelled()));
		if (e.isCancelled()) {
			if (e.getDamager().getType() == EntityType.PLAYER && NoCheatPlusSupport.isPresent())
				NCPExemptionManager.unexempt((Player) e.getDamager());
			return;
		}
		boolean ignoreArmor = victim.getMetadata("IgnoreArmor").get(0).asBoolean();
		boolean ignoreAbs = victim.getMetadata("IgnoreAbs").get(0).asBoolean();
		boolean strict = victim.getMetadata("DamageStrict").get(0).asBoolean();
		double md = strict ? victim.getMetadata("DamageAmount").get(0).asDouble() : e.getDamage();
		double df = e.getDamage(DamageModifier.BASE) != 0 ? MathUtils.round(md / e.getDamage(DamageModifier.BASE), 3)
				: 0.0d;
		if (debug) {
			Main.logger.info("Orignal BukkitDamage: " + Double.toString(e.getDamage(DamageModifier.BASE)));
			Main.logger.info("Custom MythicDamage.: " + Double.toString(md));
			Main.logger.info("DamageFactor: " + Double.toString(df));
			Main.logger.info("-----------------------------");
		}
		if (Double.isNaN(md))
			md = 0.001D;
		e.setDamage(DamageModifier.BASE, md);
		double damage = MathUtils.round(e.getDamage(DamageModifier.BASE), 3);
		for (DamageModifier modifier : DamageModifier.values()) {
			if (!e.isApplicable(modifier) || modifier.equals(DamageModifier.BASE))
				continue;
			double modF = df;
			if ((modifier.equals(DamageModifier.ARMOR) && ignoreArmor)
					|| (modifier.equals(DamageModifier.ABSORPTION) && ignoreAbs))
				modF = 0D;
			modF = MathUtils.round(modF * e.getDamage(modifier), 3);
			if (Double.isNaN(modF))
				modF = 0.001D;
			e.setDamage(modifier, modF);
			damage += e.getDamage(modifier);
		}
		if (victim.getMetadata("PreventKnockback").get(0).asBoolean()) {
			e.setCancelled(true);
			victim.damage(damage);
		} else {
			e.setDamage(victim.hasMetadata("DamageStrict") && victim.getMetadata("DamageStrict").get(0).asBoolean()
					? victim.getMetadata("DamageAmount").get(0).asDouble()
					: damage);
		}
		if (debug)
			Main.logger.info("Finaldamage amount after modifiers: " + Double.toString(damage));
		if (e.getDamager().getType() == EntityType.PLAYER && NoCheatPlusSupport.isPresent()
				&& victim.hasMetadata(meta_NCP)) {
			NCPExemptionManager.unexempt((Player) e.getDamager());
			victim.removeMetadata(meta_NCP, Main.getPlugin());
		}
	}

	public static void doDamage(SkillCaster am, AbstractEntity t, double damage, boolean ignorearmor,
								boolean preventKnockback, boolean preventImmunity, List<EntityType> ignores, boolean ignoreabs,
								boolean debug, DamageCause cause, boolean ncp, boolean strict) {
		LivingEntity target;
		am.setUsingDamageSkill(true);
		if (am instanceof ActiveMob)
			((ActiveMob) am).setLastDamageSkillAmount(damage);
		LivingEntity source = (LivingEntity) BukkitAdapter.adapt(am.getEntity());
		target = (LivingEntity) BukkitAdapter.adapt(t);
		target.setMetadata("IgnoreArmor", new FixedMetadataValue(Main.getPlugin(), ignorearmor));
		target.setMetadata("PreventKnockback", new FixedMetadataValue(Main.getPlugin(), preventKnockback));
		target.setMetadata("IgnoreAbs", new FixedMetadataValue(Main.getPlugin(), ignoreabs));
		target.setMetadata(meta_MYTHICDAMAGE, new FixedMetadataValue(Main.getPlugin(), true));
		target.setMetadata("mmcdDebug", new FixedMetadataValue(Main.getPlugin(), debug));
		target.setMetadata(meta_DAMAGECAUSE, new FixedMetadataValue(Main.getPlugin(), cause.toString()));
		target.setMetadata("DamageStrict", new FixedMetadataValue(Main.getPlugin(), strict));
		target.setMetadata(meta_MMRPGITEMDMG, new FixedMetadataValue(Main.getPlugin(), false));
		if (!ignorearmor && Main.hasRpgItems && target instanceof Player) {
			damage = rpgItemPlayerHit((Player) target, damage);
		}
		if (am.getEntity().isPlayer() && ncp && NoCheatPlusSupport.isPresent()) {
			NCPExemptionManager.exemptPermanently((Player) am.getEntity().getBukkitEntity(), CheckType.FIGHT);
			target.setMetadata(meta_NCP, new FixedMetadataValue(Main.getPlugin(), true));
		}
		if (Double.isNaN(damage))
			damage = 0.001D;
		MathUtils.round(damage, 3);
		target.setMetadata("DamageAmount", new FixedMetadataValue(Main.getPlugin(), damage));
		target.damage(damage, source);
		if (preventImmunity) {
			if (!ignores.contains(target.getType()))
				target.setNoDamageTicks(0);
		}
		am.setUsingDamageSkill(false);
	}

	public static LivingEntity getAttacker(Entity damager) {
		LivingEntity shooter = null;
		if (damager instanceof Projectile) {
			if (((Projectile) damager).getShooter() instanceof LivingEntity) {
				shooter = (LivingEntity) ((Projectile) damager).getShooter();
			}
		} else if (damager instanceof LivingEntity) {
			shooter = (LivingEntity) damager;
		}
		return shooter;
	}

	public static double rpgItemPlayerHit(Player p, double damage) {
		ItemStack[] armour = p.getInventory().getArmorContents();
		boolean useDamage = false;
		for (ItemStack pArmour : armour) {
			RPGItem pRItem = ItemManager.toRPGItem(pArmour).get();
			if (pRItem == null)
				continue;
			boolean can;
			if (!pRItem.isHitCostByDamage()) {
				can = pRItem.consumeDurability(pArmour, pRItem.getHitCost());
			} else {
				can = pRItem.consumeDurability(pArmour, (int) (pRItem.getHitCost() * damage / 100d));
			}
			if (can && pRItem.getArmour() > 0) {
				useDamage = true;
				damage -= Math.round(damage * ((pRItem.getArmour()) / 100d));
			}
		}
		if (useDamage)
			p.setMetadata(meta_MMRPGITEMDMG, new FixedMetadataValue(Main.getPlugin(), true));
		return MathUtils.round(damage, 3);
	}

	public static LivingEntity getTargetedEntity(Player player, int range) {
		BlockIterator bi;
		List<Entity> ne = Volatile.handler.getNearbyEntities(player, range);
		List<LivingEntity> entities = new ArrayList<>();
		for (Entity en : ne) {
			if ((en instanceof LivingEntity) && !en.hasMetadata(Utils.noTargetVar)) {
				entities.add((LivingEntity) en);
			}
		}
		LivingEntity target;
		bi = new BlockIterator(player, range);
		int bx;
		int by;
		int bz;
		while (bi.hasNext()) {
			Block b = bi.next();
			bx = b.getX();
			by = b.getY();
			bz = b.getZ();
			if (!b.getType().isTransparent())
				break;
			for (LivingEntity e : entities) {
				Location l = e.getLocation();
				double ex = l.getX();
				double ey = l.getY();
				double ez = l.getZ();
				if ((bx - 0.75D <= ex) && (ex <= bx + 1.75D) && (bz - 0.75D <= ez) && (ez <= bz + 1.75D)
						&& (by - 1 <= ey) && (ey <= by + 2.5D)) {
					target = e;
					if ((target != null) && ((target instanceof Player))
							&& (((Player) target).getGameMode() == org.bukkit.GameMode.CREATIVE)) {
						target = null;
					} else {
						return target;
					}
				}
			}
		}
		return null;
	}

	public static void applyInvisible(LivingEntity le, long runlater) {
		PotionEffect pe = new PotionEffect(PotionEffectType.INVISIBILITY, 2073600, 4, false, false);
		pe.apply(le);
		new BukkitRunnable() {
			@Override
			public void run() {
				le.getEquipment().clear();
			}
		}.runTaskLater(Main.getPlugin(), runlater);
	}

	public static List<Player> getPlayersInRange(Location l, double distance) {
		List<Player> players = new ArrayList<>();
		double x1 = l.getBlockX(), y1 = l.getBlockY(), z1 = l.getBlockZ();
		for (Player p : l.getWorld().getPlayers()) {
			Location l1 = p.getLocation();
			if (MathUtils.distance3D(x1, y1, z1, l1.getBlockX(), l1.getBlockY(), l1.getBlockZ()) <= distance)
				players.add(p);
		}
		return players;
	}

	public static UUID isUUID(String data) {
		UUID uuid = null;
		try {
			uuid = UUID.fromString(data);
		} catch (IllegalArgumentException ex) {
			return null;
		}
		return uuid;
	}

	public static String[] wrapStr(String s, int l) {
		String r = "";
		String d = "&&br&&";
		int ldp = 0;
		for (String t : s.split(" ", -1)) {
			if (r.length() - ldp + t.length() > l) {
				r = r + d + t;
				ldp = r.length() + 1;
			} else {
				r += (r.isEmpty() ? "" : " ") + t;
			}
		}
		return r.split(d);
	}

	public static void triggerShoot(Entity caster, Entity trigger) {
		final ActiveMob am = mobmanager.getMythicMobInstance(caster);
		if (am != null) {
			new TriggeredSkill(SkillTriggers.SHOOT, am, am.getEntity().getTarget(), true);
		}
	}

	public static float getBowTension(Player p) {
		int i1 = NMSUtils.getCurrentTick(Bukkit.getServer()), i2 = -1;
		if (p.isHandRaised() && p.hasMetadata(meta_BOWTICKSTART)) {
			i2 = p.getMetadata(meta_BOWTICKSTART).get(0).asInt();
		}
		if (i2 == -1)
			return (float) i2;
		float f1 = (float) (i1 - i2) / 20.0f;
		if ((f1 = (f1 * f1 + f1 * 2.0f) / 3.0f) > 1.0f)
			f1 = 1.0f;
		return f1;
	}

	public static int[] shuffleArray(int[] arr1) {
		int i1;
		Random r = Main.random;
		for (int i = arr1.length - 1; i > 0; i--) {
			i1 = r.nextInt(i + 1);
			if (i1 != i) {
				arr1[i1] ^= arr1[i];
				arr1[i] ^= arr1[i1];
				arr1[i1] ^= arr1[i];
			}
		}
		return arr1;
	}

	public static Object cloneObject(Object obj) {
		try {
			Object clone = obj.getClass().newInstance();
			for (Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.get(obj) == null || Modifier.isFinal(field.getModifiers()))
					continue;
				if (field.getType().isPrimitive() || field.getType().equals(String.class)
						|| field.getType().getSuperclass().equals(Number.class)
						|| field.getType().equals(Boolean.class)) {
					field.set(clone, field.get(obj));
				} else {
					Object childObj = field.get(obj);
					if (childObj == obj) {
						field.set(clone, clone);
					} else {
						field.set(clone, cloneObject(field.get(obj)));
					}
				}
			}
			return clone;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean parseNBToutcome(String s1, String s2, int i1) {
		if (0 < i1 && i1 < 7) {
			if (Character.isLetter(s1.charAt(s1.length() - 1)))
				s1 = s1.substring(0, s1.length() - 1);
			if (Character.isLetter(s2.charAt(s2.length() - 1)))
				s2 = s2.substring(0, s2.length() - 1);
			double d1 = Double.parseDouble(s1.toString()), d2 = Double.parseDouble(s2.toString());
			return d1 == d2;
		} else if (i1 == 8) {
			if (s1.toLowerCase().substring(0, 4).equals("\"rd:")) {
				s1 = s1.substring(1, s1.length() - 1);
				if (s2.startsWith("\"") && s2.endsWith("\""))
					s2 = s2.substring(1, s2.length() - 1);
				RangedDouble rd = new RangedDouble(s1.substring(3));
				if (Character.isLetter(s2.charAt(s2.length() - 1)))
					s2 = s2.substring(0, s2.length() - 1);
				double d1;
				try {
					d1 = Double.parseDouble(s2);
				} catch (Exception e) {
					return false;
				}
				return rd.equals(d1);
			}
			return s1.equals(s2);
		}
		return false;
	}

	public static boolean cmpLocByBlock(Location l1, Location l2) {
		return l1.getBlockX() == l2.getBlockX() && l1.getBlockY() == l2.getBlockY() && l1.getBlockZ() == l2.getBlockZ();
	}

	public static double getGravity(EntityType entityType) {
		switch (entityType) {
		case ARROW:
			return 0.118;
		case SNOWBALL:
			return 0.076;
		case THROWN_EXP_BOTTLE:
			return 0.157;
		case EGG:
			return 0.074;
		default:
			return 0.115;
		}
	}

	public static <E extends Enum<E>> E enum_lookup(Class<E> e, String id) {
		if (id == null)
			return null;
		E result;
		try {
			result = Enum.valueOf(e, id);
		} catch (IllegalArgumentException e1) {
			result = null;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<AbstractEntity, Double> getActiveMobThreatTable(ActiveMob am) {
		Map<AbstractEntity, Double> threattable = new HashMap<>();
		if (am != null && am.hasThreatTable()) {
			try {
				threattable = (Map<AbstractEntity, Double>) threattable_field.get(am.getThreatTable());
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return threattable;
	}

	/**
	 * 
	 * @param targeter_string {@link String}
	 * @return skill_targeter {@link io.lumine.mythic.core.skills.SkillTargeter}
	 */

	public static SkillTargeter parseSkillTargeter(String targeter_string) {
		String search = targeter_string.substring(1);
		MythicLineConfig mlc = new MythicLineConfigImpl(search);
		String name = search.contains("{") ? search.substring(0, search.indexOf("{")) : search;

		return Utils.mythicmobs.getSkillManager().getTargeter(name, mlc);
	}
	
	public static Object getTagValue(Object nbt, JsonElement json_element) {
		Object nbt_value = null;
		if (nbt != null) {
			JsonElement nbt_element = new JsonParser().parse(nbt.toString());
			System.err.println("element:" + json_element.toString());
			System.err.println("nbt:" + nbt_element.toString());

			if (json_element.isJsonArray())
				System.err.println("array");
			if (json_element.isJsonNull())
				System.err.println("null");
			if (json_element.isJsonObject())
				System.err.println("object");
			if (json_element.isJsonPrimitive())
				System.err.println("primitive");
		}
		return nbt_value;
	}
	
	static class MetaRunner {
		
		BukkitTask task;
		Map<String,Map<Plugin,MetadataValue>>entity_map;
		
		public MetaRunner() {
			
			entity_map=NMSUtils.getEntityMetadataMap(Bukkit.getServer());
			
			Predicate<String>filter=new Predicate<String>() {
				
				@Override
				public boolean test(String t) {
					return null==Bukkit.getEntity(UUID.fromString(t.split(":")[0].toLowerCase()));
				}
			};
			
			
			task=new BukkitRunnable() {
				
				@Override
				public void run() {
					
					entity_map.keySet().removeIf(filter);

				}
				
			}.runTaskTimer(Main.getPlugin(),Config.meta_delay,Config.meta_delay);
		}
	}



	// COPIED FROM io.lumine.xikage.mythicmobs.mobs.MobManager of io.lumine.xikage:MythicMobs:4.5.0
	public static AbstractLocation findSafeSpawnLocation(AbstractLocation b, int radiusXZ, int radiusY,
														 int mob_height, boolean Ymod, boolean onSurface) {
		Location base = BukkitAdapter.adapt(b);
		if (radiusXZ <= 0) {
			radiusXZ = 1;
		}

		if (radiusY <= 0) {
			radiusY = 1;
		}

		double x = base.getX() - (double)radiusXZ + (double)Numbers.randomInt(radiusXZ * 2);
		double z = base.getZ() - (double)radiusXZ + (double)Numbers.randomInt(radiusXZ * 2);
		double y;
		if (Ymod) {
			y = base.getY() - (double)radiusY + (double)Numbers.randomInt(radiusY * 2);
		} else {
			y = base.getY() + (double)Numbers.randomInt(radiusY);
		}

		Location loc = new Location(base.getWorld(), x, y, z);
		int j;
		if (loc.getBlock().getType().isSolid()) {
			j = 10;

			while(loc.getBlock().getType().isSolid()) {
				x = base.getX() - (double)radiusXZ + (double) Numbers.randomInt(radiusXZ * 2);
				z = base.getZ() - (double)radiusXZ + (double)Numbers.randomInt(radiusXZ * 2);
				if (Ymod) {
					y = base.getY() - (double)radiusY + (double)Numbers.randomInt(radiusY * 2);
				} else {
					y = base.getY() + (double)Numbers.randomInt(radiusY);
				}

				loc = new Location(base.getWorld(), x, y, z);
				--j;
				if (j == 0) {
					loc = new Location(base.getWorld(), base.getX(), base.getY() + 1.0D, base.getZ());
					break;
				}
			}
		}

		if (onSurface && !loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
			j = loc.getWorld().getHighestBlockYAt(loc);
			if ((double)j <= loc.getY()) {
				loc.setY(j + 1);
			} else {
				for(int j_ = 10; !loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid(); --j_) {
					if (j_ == 0) {
						loc = new Location(base.getWorld(), base.getX(), base.getY() + 1.0D, base.getZ());
						break;
					}

					loc.setY(loc.getY() - 1.0D);
				}
			}
		}

		return BukkitAdapter.adapt(loc);
	}

	public static AbstractLocation findSafeSpawnLocation(AbstractLocation base, int radiusXZ, int radiusY,
														 int mob_height, boolean yMod) {
		return findSafeSpawnLocation(base, radiusXZ, radiusY, mob_height, yMod, false);
	}

	public static AbstractLocation findSafeSpawnLocation(AbstractLocation base, int radiusXZ,
														 int radiusY, int mob_height) {
		return findSafeSpawnLocation(base, radiusXZ, radiusY, mob_height, true, false);
	}

	public static AbstractLocation findSafeSpawnLocation(AbstractLocation base, int radius, int mob_height) {
		return findSafeSpawnLocation(base, radius, radius, mob_height, true, false);
	}


	// COPIED FROM io.lumine.xikage.mythicmobs.skills.MobManager of io.lumine.xikage:MythicMobs:4.5.0
	public static String parseMobVariables(String s, SkillCaster caster, AbstractEntity target, AbstractEntity trigger) {
		if (s == null) {
			return null;
		} else {
			Matcher Rmatcher;
			int rand;
			Long time = System.nanoTime();
			ActiveMob am;
			Wolf w;
			int score;
			String objective;
			Objective obj;
			if (s.contains("<mob")) {
				s = s.replace("<mob.hp>", String.valueOf((int)caster.getEntity().getHealth()));
				s = s.replace("<mob.php>", String.valueOf((int)(caster.getEntity().getHealth() / caster.getEntity().getMaxHealth())));
				s = s.replace("<mob.mhp>", String.valueOf(caster.getEntity().getMaxHealth()));
				s = s.replace("<mob.thp>", String.valueOf(caster.getEntity().getHealth()));
				s = s.replace("<mob.uuid>", String.valueOf(caster.getEntity().getUniqueId().toString()));
				if (caster instanceof ActiveMob) {
					am = (ActiveMob)caster;
					if (am.getType().getDisplayName() != null) {
						s = s.replace("<mob.name>", am.getDisplayName());
					} else {
						s = s.replace("<mob.name>", "Unknown");
					}

					s = s.replace("<mob.level>", String.valueOf(am.getLevel()));
					s = s.replace("<mob.stance>", am.getStance());
					if (am.getType().getMythicEntity() instanceof BukkitWolf) {
						w = (Wolf)BukkitAdapter.adapt(am.getEntity());
						if (w.getOwner() != null) {
							s = s.replace("<mob.owner.name>", w.getOwner().getName().toString());
							s = s.replace("<mob.owner.uuid>", w.getOwner().getUniqueId().toString());
						}
					}

					if (am.hasThreatTable()) {
						if (am.getThreatTable().inCombat()) {
							s = s.replace("<mob.tt.top>", am.getThreatTable().getTopThreatHolder().getName());
						} else {
							s = s.replace("<mob.tt.top>", "Unknown");
						}
					}
				} else if (caster.getEntity().isPlayer()) {
					s = s.replace("<mob.name>", caster.getEntity().asPlayer().getName());
				}

				s = s.replace("<mob.l.w>", caster.getEntity().getWorld().getName().toString());
				if (s.contains("<mob.l.x")) {
					if (s.contains("<mob.l.x%")) {
						Rmatcher = Patterns.MSMobX.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<mob.l.x%" + Rmatcher.group(1) + ">", Integer.toString(caster.getLocation().getBlockX() + rand));
					} else {
						s = s.replace("<mob.l.x>", Integer.toString(caster.getLocation().getBlockX()));
					}
				}

				if (s.contains("<mob.l.y")) {
					if (s.contains("<mob.l.y%")) {
						Rmatcher = Patterns.MSMobY.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<mob.l.y%" + Rmatcher.group(1) + ">", Integer.toString(caster.getLocation().getBlockY() + rand));
					} else {
						s = s.replace("<mob.l.y>", Integer.toString(caster.getLocation().getBlockY()));
					}
				}

				if (s.contains("<mob.l.z")) {
					if (s.contains("<mob.l.z%")) {
						Rmatcher = Patterns.MSMobZ.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<mob.l.z%" + Rmatcher.group(1) + ">", Integer.toString(caster.getLocation().getBlockZ() + rand));
					} else {
						s = s.replace("<mob.l.z>", Integer.toString(caster.getLocation().getBlockZ()));
					}
				}

				if (s.contains("<mob.score.")) {
					for(Rmatcher = Patterns.MobScore.matcher(s); Rmatcher.find(); s = s.replace("<mob.score." + objective + ">", "" + score)) {
						objective = Rmatcher.group(1);
						obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective);
						score = 0;
						if (obj != null) {
							score = obj.getScore(caster.getEntity().getUniqueId().toString()).getScore();
						}
					}
				}
			}

			if (s.contains("<caster")) {
				s = s.replace("<caster.hp>", String.valueOf((int)caster.getEntity().getHealth()));
				s = s.replace("<caster.php>", String.valueOf((int)(caster.getEntity().getHealth() / caster.getEntity().getMaxHealth())));
				s = s.replace("<caster.mhp>", String.valueOf(caster.getEntity().getMaxHealth()));
				s = s.replace("<caster.thp>", String.valueOf(caster.getEntity().getHealth()));
				s = s.replace("<caster.uuid>", String.valueOf(caster.getEntity().getUniqueId().toString()));
				if (caster instanceof ActiveMob) {
					am = (ActiveMob)caster;
					if (am.getType().getDisplayName() != null) {
						s = s.replace("<caster.name>", am.getDisplayName());
					} else {
						s = s.replace("<caster.name>", "Unknown");
					}

					s = s.replace("<caster.level>", String.valueOf(am.getLevel()));
					s = s.replace("<caster.stance>", am.getStance());
					if (am.getType().getMythicEntity() instanceof BukkitWolf) {
						w = (Wolf)BukkitAdapter.adapt(am.getEntity());
						if (w.getOwner() != null) {
							s = s.replace("<caster.owner.name>", w.getOwner().getName().toString());
							s = s.replace("<caster.owner.uuid>", w.getOwner().getUniqueId().toString());
						}
					}

					if (am.hasThreatTable()) {
						if (am.getThreatTable().inCombat()) {
							s = s.replace("<caster.tt.top>", am.getThreatTable().getTopThreatHolder().getName());
						} else {
							s = s.replace("<caster.tt.top>", "Unknown");
						}
					}
				} else if (caster.getEntity().isPlayer()) {
					s = s.replace("<caster.name>", caster.getEntity().asPlayer().getName());
				}

				s = s.replace("<caster.l.w>", caster.getEntity().getWorld().getName().toString());
				if (s.contains("<caster.l.x")) {
					if (s.contains("<caster.l.x%")) {
						Rmatcher = Patterns.MSMobX.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<caster.l.x%" + Rmatcher.group(1) + ">", Integer.toString(caster.getLocation().getBlockX() + rand));
					} else {
						s = s.replace("<caster.l.x>", Integer.toString(caster.getLocation().getBlockX()));
					}
				}

				if (s.contains("<caster.l.y")) {
					if (s.contains("<caster.l.y%")) {
						Rmatcher = Patterns.MSMobY.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<caster.l.y%" + Rmatcher.group(1) + ">", Integer.toString(caster.getLocation().getBlockY() + rand));
					} else {
						s = s.replace("<caster.l.y>", Integer.toString(caster.getLocation().getBlockY()));
					}
				}

				if (s.contains("<caster.l.z")) {
					if (s.contains("<caster.l.z%")) {
						Rmatcher = Patterns.MSMobZ.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<caster.l.z%" + Rmatcher.group(1) + ">", Integer.toString(caster.getLocation().getBlockZ() + rand));
					} else {
						s = s.replace("<caster.l.z>", Integer.toString(caster.getLocation().getBlockZ()));
					}
				}

				if (s.contains("<caster.score.")) {
					for(Rmatcher = Patterns.MobScore.matcher(s); Rmatcher.find(); s = s.replace("<caster.score." + objective + ">", "" + score)) {
						objective = Rmatcher.group(1);
						obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective);
						score = 0;
						if (obj != null) {
							score = obj.getScore(caster.getEntity().getUniqueId().toString()).getScore();
						}
					}
				}
			}

			if (s.contains("<target") && target != null) {
				if (target != null && target.isPlayer()) {
					s = s.replace("<target.name>", target.asPlayer().getName());
				} else if (target != null && target.getName() != null) {
					s = s.replace("<target.name>", target.getName());
				}

				s = s.replace("<target.hp>", String.valueOf((int)target.getHealth()));
				s = s.replace("<target.uuid>", String.valueOf(target.getUniqueId().toString()));
				if (caster instanceof ActiveMob && ((ActiveMob)caster).hasThreatTable()) {
					s = s.replace("<target.threat>", String.valueOf(((ActiveMob)caster).getThreatTable().getThreat(target)));
				}

				s = s.replace("<target.l.w>", target.getWorld().getName().toString());
				if (s.contains("<target.l.x")) {
					if (s.contains("<target.l.x%")) {
						Rmatcher = Patterns.MSTargetX.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<target.l.x%" + Rmatcher.group(1) + ">", Integer.toString(target.getLocation().getBlockX() + rand));
					} else {
						s = s.replace("<target.l.x>", Integer.toString(target.getLocation().getBlockX()));
					}
				}

				if (s.contains("<target.l.y")) {
					if (s.contains("<target.l.y%")) {
						Rmatcher = Patterns.MSTargetY.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<target.l.y%" + Rmatcher.group(1) + ">", Integer.toString(target.getLocation().getBlockY() + rand));
					} else {
						s = s.replace("<target.l.y>", Integer.toString(target.getLocation().getBlockY()));
					}
				}

				if (s.contains("<target.l.z")) {
					if (s.contains("<target.l.z%")) {
						Rmatcher = Patterns.MSTargetZ.matcher(s);
						Rmatcher.find();
						rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
						s = s.replace("<target.l.z%" + Rmatcher.group(1) + ">", Integer.toString(target.getLocation().getBlockZ() + rand));
					} else {
						s = s.replace("<target.l.z>", Integer.toString(target.getLocation().getBlockZ()));
					}
				}

				if (s.contains("<target.score.")) {
					for(Rmatcher = Patterns.TargetScore.matcher(s); Rmatcher.find(); s = s.replace("<target.score." + objective + ">", "" + score)) {
						objective = Rmatcher.group(1);
						obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective);
						score = 0;
						if (obj != null) {
							if (target.isPlayer()) {
								score = obj.getScore(target.asPlayer().getName()).getScore();
							} else {
								score = obj.getScore(target.getUniqueId().toString()).getScore();
							}
						}
					}
				}
			}

			if (s.contains("<trigger")) {
				if (trigger != null) {
					if (trigger.isPlayer()) {
						s = s.replace("<trigger.name>", trigger.asPlayer().getName());
					} else if (trigger.getName() != null) {
						s = s.replace("<trigger.name>", trigger.getName());
					} else {
						s = s.replace("<trigger.name>", "Unknown");
					}

					s = s.replace("<trigger.hp>", String.valueOf((int)trigger.getHealth()));
					s = s.replace("<trigger.uuid>", String.valueOf(trigger.getUniqueId().toString()));
					if (caster instanceof ActiveMob && ((ActiveMob)caster).hasThreatTable()) {
						s = s.replace("<trigger.threat>", String.valueOf(((ActiveMob)caster).getThreatTable().getThreat(trigger)));
					}

					s = s.replace("<trigger.l.w>", trigger.getWorld().getName().toString());
					if (s.contains("<trigger.l.x")) {
						if (s.contains("<trigger.l.x%")) {
							Rmatcher = Patterns.MSTriggerX.matcher(s);
							Rmatcher.find();
							rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
							s = s.replace("<trigger.l.x%" + Rmatcher.group(1) + ">", Integer.toString(trigger.getLocation().getBlockX() + rand));
						} else {
							s = s.replace("<trigger.l.x>", Integer.toString(trigger.getLocation().getBlockX()));
						}
					}

					if (s.contains("<trigger.l.y")) {
						if (s.contains("<trigger.l.y%")) {
							Rmatcher = Patterns.MSTriggerY.matcher(s);
							Rmatcher.find();
							rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
							s = s.replace("<trigger.l.y%" + Rmatcher.group(1) + ">", Integer.toString(trigger.getLocation().getBlockY() + rand));
						} else {
							s = s.replace("<trigger.l.y>", Integer.toString(trigger.getLocation().getBlockY()));
						}
					}

					if (s.contains("<trigger.l.z")) {
						if (s.contains("<trigger.l.z%")) {
							Rmatcher = Patterns.MSTriggerZ.matcher(s);
							Rmatcher.find();
							rand = Numbers.randomInt(2) == 1 ? Numbers.randomInt(1 + Integer.parseInt(Rmatcher.group(1))) : 0 - Numbers.randomInt(Integer.parseInt(Rmatcher.group(1)));
							s = s.replace("<trigger.l.z%" + Rmatcher.group(1) + ">", Integer.toString(trigger.getLocation().getBlockZ() + rand));
						} else {
							s = s.replace("<trigger.l.z>", Integer.toString(trigger.getLocation().getBlockZ()));
						}
					}

					if (s.contains("<trigger.score.")) {
						for(Rmatcher = Patterns.TriggerScore.matcher(s); Rmatcher.find(); s = s.replace("<trigger.score." + objective + ">", "" + score)) {
							objective = Rmatcher.group(1);
							obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective);
							score = 0;
							if (obj != null) {
								if (trigger.isPlayer()) {
									score = obj.getScore(trigger.asPlayer().getName()).getScore();
								} else {
									score = obj.getScore(trigger.getUniqueId().toString()).getScore();
								}
							}
						}
					}
				} else {
					s = s.replace("<trigger.name>", "Unknown");
				}
			}

			s = SkillString.parseMessageSpecialChars(s);
			if (s.contains("<random")) {
				for(Matcher pMatcher = Patterns.VariableRanges.matcher(s); pMatcher.find(); s = s.replace(pMatcher.group(0), "" + score)) {
					int min = Integer.parseInt(pMatcher.group(1));
					score = Integer.parseInt(pMatcher.group(2));
					score = Numbers.randomInt(score - min + 1) + min;
				}
			}

			if (s.contains("<global.score.")) {
				for(Rmatcher = Patterns.GlobalScore.matcher(s); Rmatcher.find(); s = s.replace("<global.score." + objective + ">", "" + score)) {
					objective = Rmatcher.group(1);
					obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective);
					score = 0;
					if (obj != null) {
						score = obj.getScore("__GLOBAL__").getScore();
					}
				}
			}

			String entry;
			if (s.contains("<score.")) {
				for(Rmatcher = Patterns.GenericScore.matcher(s); Rmatcher.find(); s = s.replace("<score." + objective + "." + entry + ">", "" + score)) {
					objective = Rmatcher.group(1);
					entry = Rmatcher.group(2);
					obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(objective);
					score = 0;
					if (obj != null) {
						score = obj.getScore(entry).getScore();
					}
				}
			}

			return s;
		}
	}
	
}

