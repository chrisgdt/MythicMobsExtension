package com.gmail.berndivader.mythicmobsext.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.jboolexpr.BooleanExpression;
import com.gmail.berndivader.mythicmobsext.jboolexpr.MalformedBooleanException;
import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.backbags.BackBagHelper;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.items.HoldingItem;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.SkillString;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;

@ExternalAnnotation(name = "testitemfor,ownsitem,ownsitemsimple,iteminhand", author = "BerndiVader")
public class HasItemCondition extends AbstractCustomCondition implements IEntityCondition {

	private String conditionLine, meta_var;
	private boolean store_result;
	private List<HoldingItem> holdinglist;

	public HasItemCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.holdinglist = new ArrayList<>();
		store_result = !(this.meta_var = mlc.getString("var", "")).isEmpty();
		String tmp = mlc.getString(new String[] { "list", "l" }, null);
		if (tmp == null) {
			tmp = "\"where=" + mlc.getString("where", "ANY");
			tmp += ";material=" + mlc.getString("material", "ANY");
			tmp += ";amount=" + mlc.getString("amount", ">0");
			tmp += ";slot=" + mlc.getString("slot", "-1");
			tmp += ";name=" + mlc.getString("name", "ANY");
			tmp += ";enchant=" + mlc.getString("enchant", "ANY");
			tmp += ";lore=" + mlc.getString("lore", "ANY");
			tmp += ";bagname=" + mlc.getString("bagname", BackBagHelper.str_name) + "\"";
			tmp = SkillString.unparseMessageSpecialChars(tmp);
		}
		this.conditionLine = SkillString.parseMessageSpecialChars(tmp);
		String[] list = tmp.split("&&|\\|\\|");
		for (int a = 0; a < list.length; a++) {
			String parse = list[a];
			HoldingItem holding = new HoldingItem();
			parse = SkillString.parseMessageSpecialChars(parse);
			this.conditionLine = this.conditionLine.replaceFirst(Pattern.quote(parse), "\\$" + Integer.toString(a));
			if (parse.startsWith("\"") && parse.endsWith("\""))
				HoldingItem.parse(parse.substring(1, parse.length() - 1), holding);
			this.holdinglist.add(holding);
		}
	}

	@Override
	public boolean check(AbstractEntity t) {
		if (t.isLiving()) {
			String c = this.conditionLine;
			final LivingEntity target = (LivingEntity) t.getBukkitEntity();
			for (int i1 = 0; i1 < holdinglist.size(); i1++) {
				boolean bool = false;
				HoldingItem holding = holdinglist.get(i1).clone();
				SkillMetadata data = new SkillMetadata(SkillTrigger.API, new GenericCaster(t), t);
				holding.parseSlot(data, t);
				String bag_name = holding.getBagName();
				if (bag_name != null)
					holding.setBagName(PlaceholderString.of(bag_name).get(data, t));
				List<ItemStack> contents = HoldingItem.getContents(holding, target);
				for (int i2 = 0; i2 < contents.size(); i2++) {
					if (bool = holding.stackMatch(contents.get(i2), false)) {
						if (store_result)
							target.setMetadata(meta_var,
									new FixedMetadataValue(Main.getPlugin(), holding.getWhere().name()));
						break;
					}
				}
				c = c.replaceFirst("\\$" + Integer.toString(i1), Boolean.toString(bool));
			}
			BooleanExpression be;
			try {
				be = BooleanExpression.readLR(c);
			} catch (MalformedBooleanException e) {
				Main.logger.warning("Invalid bool expression: " + this.conditionLine);
				return false;
			}
			return be.booleanValue();
		}
		return false;
	}

}
