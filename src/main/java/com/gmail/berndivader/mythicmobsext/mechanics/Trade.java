package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

@ExternalAnnotation(name = "trade", author = "Seyarada")
public class Trade extends SkillMechanic implements ITargetedEntitySkill {
	private PlaceholderString text;
	String title;
	List<String> tradesRaw = new ArrayList<>();

	public Trade(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
		title = mlc.getString(new String[] { "title", "t"}, "Trades");
		for (int index = 1; index <= 10; index++) {
			String x = mlc.getString(new String[] {String.valueOf(index)}, "none");
			if (!x.equals("none")) tradesRaw.add(x);
			}
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity playerEntity) {
		if (playerEntity.isPlayer()) {
			Player player = (Player) playerEntity.getBukkitEntity();
			List<MerchantRecipe> merchantRecipes = getRecipes(data);
			
			Entity villagerEntity = data.getCaster().getEntity().getBukkitEntity();
			if(villagerEntity instanceof Villager) {
				((Villager)villagerEntity).setRecipes(merchantRecipes);
				player.openMerchant(((Villager)villagerEntity), true);
			} else {
				Merchant merchant = Bukkit.createMerchant(title);
				merchant.setRecipes(merchantRecipes);
				player.openMerchant(merchant, true);
			}
			
			
		}
		return SkillResult.SUCCESS;
	}
	
	public List<MerchantRecipe> getRecipes(SkillMetadata data) {
		List<MerchantRecipe> merchantRecipes = new ArrayList<MerchantRecipe>();
		
		for (String trades : tradesRaw) {
			
			ItemStack result = null, price1 = null, price2 = null;
        	ItemStack finalResult = null,finalPrice1 = null,finalPrice2 = null;
        	Integer uses = 9999;
        	Boolean xp = true;
        	
            for(String trade : trades.split(",")) {	
            	String resultString = null, price1String = null, price2String = null;
            	
            	
            	String[] n = trade.split(":");
            	String k = trade.split(":")[0];
            	String l = trade.split(":")[1];
            	
            	Integer amount = 1;
            	if (n.length > 2) {
        			text = new PlaceholderStringImpl(SkillString.unparseMessageSpecialChars(n[2]));
            		if (text.get(data).contains("to")) {
            			amount = MathUtils.randomRangeInt(text.get(data));
            			
            		}else {
            			amount = Integer.valueOf(text.get(data));
            			
            		}
            		
            	}
            	
            	if (k.equals("result")) {
            		text = new PlaceholderStringImpl(SkillString.unparseMessageSpecialChars(l));
            		resultString = this.text.get(data);

            	}
            	else if (k.equals("price")) {
            		text = new PlaceholderStringImpl(SkillString.unparseMessageSpecialChars(l));
            		price1String = this.text.get(data);
            	}
            	else if (k.equals("price1")) {
            		text = new PlaceholderStringImpl(SkillString.unparseMessageSpecialChars(l));
            		price1String = this.text.get(data);
            	}
            	else if (k.equals("price2")) {
            		text = new PlaceholderStringImpl(SkillString.unparseMessageSpecialChars(l));
            		price2String = this.text.get(data);
            	}
            	else if (k.equals("uses")) uses = Integer.valueOf(l);
            	else if (k.equals("xp")) xp = Boolean.valueOf(l);
            	
            	result = getItem(resultString,amount);
            	price1 = getItem(price1String,amount);
            	price2 = getItem(price2String,amount);
            	
            	if (result!=null) finalResult = result;
            	if (price1!=null) finalPrice1 = price1;
            	if (price2!=null) finalPrice2 = price2;
            	resultString = null; price1String = null; price2String = null;
            	
            }
        	MerchantRecipe recipe = new MerchantRecipe(finalResult, uses);
        	recipe.setVillagerExperience(0);
			recipe.setExperienceReward(xp);
			recipe.addIngredient(finalPrice1);
			recipe.setVillagerExperience(5);
			if (finalPrice2!=null) recipe.addIngredient(finalPrice2);
			merchantRecipes.add(recipe);
        }
		return merchantRecipes;
	}
	
	public ItemStack getItem(String i, Integer amount) {
		if (i == null) return null;
		
		ItemStack item = null;
		try {
			Material baseMaterial = Material.valueOf(i);
			item = new ItemStack(baseMaterial, amount);
		} catch (Exception e) {
			Optional<MythicItem> t = Utils.mythicmobs.getItemManager().getItem(i);
            item = BukkitAdapter.adapt(t.get().generateItemStack(amount));
		}
		return item;
	}
}
