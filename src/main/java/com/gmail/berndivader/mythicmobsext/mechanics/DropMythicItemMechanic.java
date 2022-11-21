package com.gmail.berndivader.mythicmobsext.mechanics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.skills.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.drops.IMessagingDrop;
import io.lumine.mythic.api.drops.IMultiDrop;
import io.lumine.mythic.core.drops.InvalidDrop;
import io.lumine.mythic.core.drops.LootBag;
import io.lumine.mythic.core.drops.droppables.ExperienceDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;

@ExternalAnnotation(name = "dropmythicitem", author = "BerndiVader")
public class DropMythicItemMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
	String[] tags;
	PlaceholderString str_types;
	String dropname;
	boolean tag, give, stackable, silent;
	String amount;

	public DropMythicItemMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.str_types = mlc.getPlaceholderString(new String[] { "mythicitem", "item", "itemtype", "type", "t", "i" },
				"");
		this.tags = mlc.getString(new String[] { "tags", "tag" }, "").split(",");
		this.silent = mlc.getBoolean("silent", false);
		this.tag = tags[0].length() > 0;
		this.give = mlc.getBoolean("give", false);
		this.stackable = mlc.getBoolean(new String[] { "stackable", "sa" }, true);
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
		String[] types = this.str_types.get(data).split(",");
		LootBag loot = makeLootBag(data, types, data.getTrigger(), tag, tags, this.stackable);
		giveOrDrop(BukkitAdapter.adapt(target), null, loot, give, tag, stackable, tags, silent);
		return SkillResult.SUCCESS;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		String[] types = this.str_types.get(data, target).split(",");
		LootBag loot = makeLootBag(data, types, target, tag, tags, this.stackable);
		if (target.isPlayer()) {
			Player player = (Player) target.getBukkitEntity();
			if (player.isOnline())
				giveOrDrop(player.getLocation(), player, loot, give, tag, stackable, tags, silent);
		} else {
			giveOrDrop(target.getBukkitEntity().getLocation(), null, loot, give, tag, stackable, tags, silent);
		}
		return SkillResult.SUCCESS;
	}

	private static LootBag makeLootBag(SkillMetadata data, String[] types, AbstractEntity target, boolean tag,
			String[] tags, boolean stackable) {
		LootBag loot = new LootBag(new DropMetadataImpl(data.getCaster(), target));
		Map<Class, Drop> intangibleDrops = new HashMap<Class, Drop>();
		List<Drop> itemDrops = new ArrayList<Drop>();

		for (int i1 = 0; i1 < types.length; i1++) {
			String item = types[i1].replaceAll(":", " ");
			String arr1[] = item.split(" ");
			Drop drop = NMSUtils.getDrop(item);
			if (drop instanceof InvalidDrop)
				continue;

			double amount = arr1.length == 1 ? 1.0D : MathUtils.randomRangeInt(arr1[1]);
			if (amount < 1)
				continue;
			drop.setAmount(amount);
			if (drop instanceof IItemDrop) {
				itemDrops.add(drop);
			} else if (drop instanceof IMultiDrop) {
				LootBag loot1 = ((IMultiDrop) drop).get(new DropMetadataImpl(data.getCaster(), target));
				for (Drop d1 : loot1.getDrops()) {
					if (d1 instanceof IItemDrop) {
						itemDrops.add(d1);
					} else {
						intangibleDrops.merge(d1.getClass(), d1, (o, n) -> o.addAmount(n));
					}
				}
			} else {
				intangibleDrops.merge(drop.getClass(), drop, (o, n) -> o.addAmount(n));
			}
		}
		loot.setLootTable(itemDrops);
		loot.setLootTableIntangible(intangibleDrops);
		return loot;
	}

	static ItemStack createItemStack(ItemStack i, boolean tag, boolean stackable, String[] tags) {
		ItemStack is = new ItemStack(i);
		if (tag) {
			is = new ItemStack(NMSUtils.makeReal(i));
			for (int i2 = 0; i2 < tags.length; i2++) {
				String[] arr2 = tags[i2].split(":");
				NMSUtils.setMeta(is, arr2[0], arr2.length > 1 ? arr2[1] : is.getType().toString());
			}
		}
		if (!stackable) {
			is = new ItemStack(NMSUtils.makeReal(is));
			UUID uuid = UUID.randomUUID();
			String most = Long.toString(uuid.getMostSignificantBits()),
					least = Long.toString(uuid.getLeastSignificantBits());
			NMSUtils.setMeta(is, "RandomMost", most);
			NMSUtils.setMeta(is, "RandomLeast", least);
		}
		return is;
	}

	static void giveOrDrop(Location l, Player player, LootBag loot, boolean give, boolean tag, boolean stackable,
			String[] tags, boolean silent) {
		boolean isPresent = player != null;
		HashMap<IMessagingDrop, Double> msgDrops = new HashMap<IMessagingDrop, Double>();
		World w = l.getWorld();
		for (Drop drop : loot.getDrops()) {
			if (drop instanceof IItemDrop) {
				ItemStack is = createItemStack(BukkitAdapter.adapt(((IItemDrop) drop).getDrop(loot.getMetadata(), 1)), tag,
						stackable, tags);
				if (give && isPresent && player.getInventory().firstEmpty() > -1) {
					player.getInventory().addItem(is.clone());
				} else {
					w.dropItem(l, is.clone());
				}
			} else if (drop instanceof ExperienceDrop) {
				if (isPresent && give) {
					player.giveExp((int) drop.getAmount());
				} else {
					ExperienceOrb exp = (ExperienceOrb) w.spawnEntity(l, EntityType.EXPERIENCE_ORB);
					exp.setExperience((int) drop.getAmount());
				}

			} else if (drop instanceof IIntangibleDrop) {
				if (isPresent)
					((IIntangibleDrop) drop).giveDrop(BukkitAdapter.adapt(player), loot.getMetadata(), 1);
			}
			if (drop instanceof IMessagingDrop) {
				msgDrops.merge((IMessagingDrop) drop, drop.getAmount(), (n, o) -> n + o);
			}
		}
		if (isPresent && !silent && msgDrops.size() > 0) {
			for (Map.Entry<IMessagingDrop, Double> entry : msgDrops.entrySet()) {
				player.sendMessage(entry.getKey().getRewardMessage(loot.getMetadata(),
						entry.getValue()));
			}
		}
	}

}