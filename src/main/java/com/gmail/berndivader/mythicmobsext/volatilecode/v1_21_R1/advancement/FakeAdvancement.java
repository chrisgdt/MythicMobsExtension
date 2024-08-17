package com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1.advancement;

import java.util.*;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

public class FakeAdvancement {
	private com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1.advancement.FakeDisplay display;

	public FakeAdvancement(com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1.advancement.FakeDisplay display) {
		this.display = display;
	}

	public com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1.advancement.FakeDisplay getDisplay() {
		return this.display;
	}

	public void displayToast(Player player) {
		ResourceLocation key = ResourceLocation.fromNamespaceAndPath("mme", "notification");
		FakeDisplay display = this.getDisplay();
		ResourceLocation backgroundTexture = null;
		boolean useBackground = display.getBackgroundTexture() != null;
		if (useBackground)
			backgroundTexture = ResourceLocation.parse(display.getBackgroundTexture());
		Map<String, Criterion<?>> criterias = new HashMap<>();
		Criterion<?> criterion = new Criterion<>(null, // todo
				new CriterionTriggerInstance() {
			// public ResourceLocation getCriterion() { return new ResourceLocation("minecraft", "impossible"); }
			@Override
			public void validate(CriterionValidator validator) { return; }
		});
		criterias.put("for_free", criterion);

		List<String> innerList = new ArrayList<>();
		innerList.add("for_free");
		List<List<String>> outerList = new ArrayList<>();
		outerList.add(innerList);
		AdvancementRequirements requirements = new AdvancementRequirements(outerList);
		DisplayInfo nmsDisplay = new DisplayInfo(
				CraftItemStack.asNMSCopy(display.getIcon()),
				(net.minecraft.network.chat.Component) display.getTitle().getBaseComponent(),
				(net.minecraft.network.chat.Component) display.getDescription().getBaseComponent(), Optional.ofNullable(backgroundTexture),
				display.getFrame().getNMS(), true, false, true);
		AdvancementRewards rewards = new AdvancementRewards(0, new ArrayList<>(), new ArrayList<>(), null);
		Advancement nmsAdvancement = new Advancement(
				Optional.of(key),
				Optional.of(nmsDisplay),
				rewards,
				criterias,
				requirements,
				false,
				Optional.empty());

		HashMap<ResourceLocation, AdvancementProgress> progresses = new HashMap<>();
		AdvancementProgress progress = new AdvancementProgress();
		progress.update(requirements);

		progress.getCriterion("for_free").grant();
		progresses.put(key, progress);
		ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false,
				Collections.singletonList(new AdvancementHolder(key, nmsAdvancement)), new HashSet<>(), progresses);
		((CraftPlayer) player).getHandle().connection.send(packet);
		HashSet<ResourceLocation> rm = new HashSet<>();
		rm.add(key);
		progresses.clear();
		packet = new ClientboundUpdateAdvancementsPacket(false, new ArrayList<>(), rm, progresses);
		((CraftPlayer) player).getHandle().connection.send(packet);
	}

}
