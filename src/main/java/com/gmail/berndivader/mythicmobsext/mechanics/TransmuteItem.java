package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;

import java.util.Optional;

@ExternalAnnotation(name = "transmuteitem", author = "Seyarada")
public class TransmuteItem extends SkillMechanic implements ITargetedEntitySkill {

	PlaceholderInt amount;
	ItemStack baseItem;
	ItemStack resultItem;
	Material resultMaterial;
	String resultMLC;
	
	public TransmuteItem(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		amount = PlaceholderInt.of(mlc.getString(new String[] {"amount", "a"}, "-1"));
		
		String baseMLC = mlc.getString(new String[] {"item", "i"}, "STONE");
		try {
			Material baseMaterial = Material.valueOf(baseMLC);
			baseItem = new ItemStack(baseMaterial);
		} catch (Exception e) {
			Optional<MythicItem> t = Utils.mythicmobs.getItemManager().getItem(baseMLC);
            ItemStack item = BukkitAdapter.adapt(t.get().generateItemStack(1));
            baseItem = item;
		}
				
		resultMLC = mlc.getString(new String[] {"result", "r"}, "DIRT");
	}
	
	public ItemStack generateItemStack(int quantity) {
		
		try {
			Material resultMaterial = Material.valueOf(resultMLC);
			resultItem = new ItemStack(resultMaterial, quantity);
		} catch (Exception e) {
			Optional<MythicItem> t = Utils.mythicmobs.getItemManager().getItem(resultMLC);
            ItemStack mythicItem = BukkitAdapter.adapt(t.get().generateItemStack(quantity));
            resultItem = mythicItem;
		}
		
		return resultItem;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity p) {
		
		int altAmount = Integer.valueOf(amount.get(data, p));
		if (p.isPlayer()) {
			Player player = (Player) p.getBukkitEntity();
			
			ItemStack[] baseContent = player.getInventory().getContents();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				if(baseContent[i] != null && baseContent[i].isSimilar(baseItem)) {
		
					int quantity = baseContent[i].getAmount();
					if(altAmount < 0) {
						resultItem = generateItemStack(quantity);
						player.getInventory().setItem(i, resultItem);
					}		
					
					// Stack is larger than amount
					else if(quantity > altAmount && altAmount != 0) {
						baseContent[i].setAmount(quantity-altAmount);
						player.getInventory().addItem(generateItemStack(altAmount));
						altAmount = 0;
					}
					
					// Stack is smaller than amount
					else if (altAmount != 0) {
						resultItem = generateItemStack(quantity);
						player.getInventory().setItem(i, resultItem);
						altAmount = altAmount - quantity;
					}
				}
			}
		}
		
		return SkillResult.SUCCESS;
	}

}