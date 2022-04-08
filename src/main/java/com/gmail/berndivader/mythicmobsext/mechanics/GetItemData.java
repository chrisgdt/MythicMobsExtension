package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;


@ExternalAnnotation(name = "getitemdata,gid", author = "Seyarada")
public class GetItemData extends VariableMechanic implements ITargetedEntitySkill {

	private final String where;
	private final String searchKey;
	private final String get;
	private ItemStack mythicItem;
	private VariableType type;
	private int loreLine;
	private String loreRegex;
	
	
	SkillMetadata data;
	AbstractEntity target;
	
	public GetItemData(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.where = mlc.getString(new String[] { "where", "w" }, "HAND");
		this.searchKey = mlc.getString(new String[] { "key", "k" }, "Hello");
		this.get = mlc.getString(new String[] { "get", "g" }, "amount");
		this.loreLine = mlc.getInteger(new String[] { "loreline", "line" }, 0);
		this.loreRegex = mlc.getString(new String[] { "loreregex", "regex", "lr" }, null);
		
		String strType = mlc.getString(new String[]{"type", "t"}, VariableType.INTEGER.toString());
	    try {
	       this.type = VariableType.valueOf(strType.toUpperCase());
	    } catch (Exception e) {
	       MythicLogger.errorMechanicConfig(this, mlc, "'" + strType + "' is not a valid variable type.");
	    }
	    
	    String baseMLC = mlc.getString(new String[] {"item", "i"}, "STONE");
	    try {
			Material baseMaterial = Material.valueOf(baseMLC);
			mythicItem = new ItemStack(baseMaterial);
		} catch (Exception e) {
			Optional<MythicItem> t = Utils.mythicmobs.getItemManager().getItem(baseMLC);
            ItemStack item = BukkitAdapter.adapt(t.get().generateItemStack(1));
            mythicItem = item;
		}
	    
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata arg0, AbstractEntity arg1) {
		this.data = arg0; this.target = arg1;
		ItemStack iS = getItem();
		switch (this.get) {
		
			case "amount":
				storeVariable(String.valueOf(iS.getAmount()));
				break;
			case "count":				
		        ItemStack[] items = ((Player)this.target.getBukkitEntity()).getInventory().getContents();
		        int has = 0;
		        for (ItemStack item : items)
		        {
		            if ((item != null) && (item.isSimilar(mythicItem)) && (item.getAmount() > 0)) 
		            	has += item.getAmount();
		        }
				storeVariable(String.valueOf(has));
				break;
			case "nbt":
				storeVariable(readNBT(iS));
				break;
			case "material":
				storeVariable(String.valueOf(iS.getType()));
				break;
			case "lore":
				ItemMeta itemMeta = iS.getItemMeta();
				if (itemMeta.hasLore()) {
					List<String> loreArray = itemMeta.getLore();
					if(loreArray.size() > loreLine) {
						String line = loreArray.get(loreLine);
						if(loreRegex == null)
							storeVariable(line);
						else {
							Pattern pattern = Pattern.compile(loreRegex, Pattern.CASE_INSENSITIVE);
							Matcher matcher = pattern.matcher(line);
							if(matcher.find()) {
								storeVariable(matcher.group(0));
							}
						}
					}
				}
				break;
			case "name":
				ItemMeta itemMetaN = iS.getItemMeta();
				if (itemMetaN.hasDisplayName()) {
					storeVariable(itemMetaN.getDisplayName());
				} else {
					storeVariable(iS.getType().name());
				}
				break;
			default:
				break;
		
		}
		
		return SkillResult.ERROR;
	}

	public ItemStack getItem() {
		
		LivingEntity entity = (LivingEntity) target.getBukkitEntity();
		EntityEquipment equipment = entity.getEquipment();
		
		switch (this.where) {
			case "HAND":
				return equipment.getItemInMainHand().clone();
			case "OFFHAND":
				return equipment.getItemInOffHand().clone();
			case "HELMET":
				return equipment.getHelmet().clone();
			case "CHESTPLATE":
				return equipment.getChestplate().clone();
			case "LEGGINGS":
				return equipment.getLeggings().clone();
			case "BOOTS":
				return equipment.getBoots().clone();
		} return null;
		
	}
	
	public void storeVariable(String value) {
		VariableRegistry variables = getVariableManager().getRegistry(this.scope, this.data, this.target);
	    if (variables == null)
	    	MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable registry (MME)");
	    else {
	    	Variable var = null;
	        if (this.type != VariableType.INTEGER && this.type != VariableType.FLOAT)
	            var = Variable.ofType(this.type, value, this.duration);
	        else if (this.type == VariableType.INTEGER) {
	        	if(value=="null"||value==null) value = "0";
	            var = Variable.ofType(this.type, Integer.valueOf(value), this.duration);
	        }
	        else if (this.type == VariableType.FLOAT) {
	        	if(value=="null"||value==null) value = "0";
	        	var = Variable.ofType(this.type, Float.valueOf(value), this.duration);
	        }
	        variables.put(this.key, var);
	      }
	}
	
	public String readNBT(ItemStack iS) {
		if(iS==null) return "null";
		CompoundTag a = Utils.mythicmobs.getVolatileCodeHandler().getItemHandler().getNBTData(iS);
		if(a.containsKey(this.searchKey)) return a.getString(this.searchKey);
		return "null";
	}
}