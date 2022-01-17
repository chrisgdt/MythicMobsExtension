package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.advancement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.LootSerializationContext;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutAdvancements;
import net.minecraft.resources.MinecraftKey;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

public class FakeAdvancement {
	private com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.advancement.FakeDisplay display;

	public FakeAdvancement(com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.advancement.FakeDisplay display) {
		this.display = display;
	}

	public com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.advancement.FakeDisplay getDisplay() {
		return this.display;
	}

	public void displayToast(Player player) {
		MinecraftKey key = new MinecraftKey("mme", "notification");
		FakeDisplay display = this.getDisplay();
		MinecraftKey backgroundTexture = null;
		boolean useBackground = display.getBackgroundTexture() != null;
		if (useBackground)
			backgroundTexture = new MinecraftKey(display.getBackgroundTexture());
		HashMap<String, Criterion> criterias = new HashMap<String, Criterion>();
		String[][] requirements = new String[][] {};
		criterias.put("for_free", new Criterion(new CriterionInstance() {
			public MinecraftKey a() {
				return new MinecraftKey("minecraft", "impossible");
			}
			// MODIFIED
			@Override
			public JsonObject a(LootSerializationContext arg0) {
				return null;
			}
			// MODIFIED
		}));
		ArrayList<String[]> fixed = new ArrayList<>();
		fixed.add(new String[] { "for_free" });
		requirements = Arrays.stream(fixed.toArray()).toArray(n -> new String[n][]);
		AdvancementDisplay nmsDisplay = new AdvancementDisplay(
				CraftItemStack.asNMSCopy(display.getIcon()),
				(IChatBaseComponent) display.getTitle().getBaseComponent(),
				(IChatBaseComponent) display.getDescription().getBaseComponent(), backgroundTexture,
				display.getFrame().getNMS(), true, false, true);
		Advancement nmsAdvancement = new Advancement(key, null, nmsDisplay,
				new AdvancementRewards(0, new MinecraftKey[0], new MinecraftKey[0], null), criterias, requirements);

		HashMap<MinecraftKey, AdvancementProgress> progresses = new HashMap<>();
		AdvancementProgress progress = new AdvancementProgress();
		progress.a(criterias, requirements);
		progress.c("for_free").b(); // getCriterionProgress
		progresses.put(key, progress);
		PacketPlayOutAdvancements packet = new PacketPlayOutAdvancements(false,
				Arrays.asList(new Advancement[] { nmsAdvancement }), new HashSet<>(), progresses);
		((CraftPlayer) player).getHandle().b.sendPacket(packet); // playerConnection
		HashSet<MinecraftKey> rm = new HashSet<>();
		rm.add(key);
		progresses.clear();
		packet = new PacketPlayOutAdvancements(false, new ArrayList<>(), rm, progresses);
		((CraftPlayer) player).getHandle().b.sendPacket(packet); // playerConnection
	}

}
