package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.advancement;

import java.util.*;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

public class FakeAdvancement {
	private com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.advancement.FakeDisplay display;

	public FakeAdvancement(com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.advancement.FakeDisplay display) {
		this.display = display;
	}

	public com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.advancement.FakeDisplay getDisplay() {
		return this.display;
	}

	public void displayToast(Player player) {
		ResourceLocation key = new ResourceLocation("mme", "notification");
		FakeDisplay display = this.getDisplay();
		ResourceLocation backgroundTexture = null;
		boolean useBackground = display.getBackgroundTexture() != null;
		if (useBackground)
			backgroundTexture = new ResourceLocation(display.getBackgroundTexture());
		HashMap<String, Criterion> criterias = new HashMap<String, Criterion>();
		String[][] requirements = new String[][] {};
		criterias.put("for_free", new Criterion(new CriterionTriggerInstance() {
			public ResourceLocation a() {
				return new ResourceLocation("minecraft", "impossible");
			}

			@Override
			public ResourceLocation getCriterion() {
				return null;
			}

			// MODIFIED
			@Override
			public JsonObject serializeToJson(SerializationContext arg0) {
				return null;
			}
			// MODIFIED
		}));
		ArrayList<String[]> fixed = new ArrayList<String[]>();
		fixed.add(new String[] { "for_free" });
		requirements = Arrays.stream(fixed.toArray()).toArray(String[][]::new);
		DisplayInfo nmsDisplay = new DisplayInfo(
				CraftItemStack.asNMSCopy(display.getIcon()),
				(net.minecraft.network.chat.Component) display.getTitle().getBaseComponent(),
				(net.minecraft.network.chat.Component) display.getDescription().getBaseComponent(), backgroundTexture,
				display.getFrame().getNMS(), true, false, true);
		Advancement nmsAdvancement = new Advancement(key, null, nmsDisplay,
				new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], null), criterias, requirements);

		HashMap<ResourceLocation, AdvancementProgress> progresses = new HashMap<>();
		AdvancementProgress progress = new AdvancementProgress();
		progress.update(criterias, requirements);

		progress.getCriterion("for_free").grant();
		progresses.put(key, progress);
		ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false,
				Collections.singletonList(nmsAdvancement), new HashSet<>(), progresses);
		((CraftPlayer) player).getHandle().connection.send(packet);
		HashSet<ResourceLocation> rm = new HashSet<>();
		rm.add(key);
		progresses.clear();
		packet = new ClientboundUpdateAdvancementsPacket(false, new ArrayList<>(), rm, progresses);
		((CraftPlayer) player).getHandle().connection.send(packet);
	}

}
