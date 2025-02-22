package com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1.advancement;

import com.gmail.berndivader.mythicmobsext.utils.JSONMessage;
import javax.annotation.Nullable;

import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FakeDisplay {
	private Material iconID;
	private ItemStack icon;
	private JSONMessage title, description;
	private AdvancementFrame frame;
	private String backgroundTexture;

	public FakeDisplay(Material icon, String title, String description, AdvancementFrame frame,
			String backgroundTexture) {
		this.icon = new ItemStack(icon);
		if (title.contains("\u00a7"))
			title = String.valueOf(title) + "\u00a7a";
		this.title = new JSONMessage("{\"text\":\"" + title.replaceAll("\"", "\\\"") + "\"}");
		this.description = new JSONMessage("{\"text\":\"" + description.replaceAll("\"", "\\\"") + "\"}");
		this.frame = frame;
		this.backgroundTexture = backgroundTexture;
	}

	public ItemStack getIcon() {
		if (this.icon == null && this.iconID != null)
			this.icon = new ItemStack(this.iconID);
		return this.icon;
	}

	public JSONMessage getTitle() {
		return this.title;
	}

	public JSONMessage getDescription() {
		return this.description;
	}

	public AdvancementFrame getFrame() {
		return this.frame;
	}

	@Nullable
	public String getBackgroundTexture() {
		return this.backgroundTexture;
	}

	public static enum AdvancementFrame {
		TASK(AdvancementType.TASK), GOAL(AdvancementType.GOAL), CHALLENGE(AdvancementType.CHALLENGE);

		private AdvancementType nms;

		private AdvancementFrame(AdvancementType nms) {
			this.nms = nms;
		}

		public AdvancementType getNMS() {
			return this.nms;
		}
	}

}
