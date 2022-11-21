package com.gmail.berndivader.mythicmobsext.mechanics;

import java.io.File;
import java.util.Optional;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;

@ExternalAnnotation(name = "ontrade", author = "Seyarada")
public class OnTrade extends Aura implements ITargetedEntitySkill {
   protected Optional<Skill> onTradeSkill = Optional.empty();
   protected String onTradeSkillName;
   protected boolean cancelEvent;
   protected boolean forceAsPower;
   protected int oldXP;
   protected int newXP;

   public OnTrade(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
       super(manager, file, skill, mlc);
       this.line = skill;
      this.onTradeSkillName = mlc.getString(new String[]{"ontradeskill", "ontrade", "os", "s", "skill"});
      this.cancelEvent = mlc.getBoolean(new String[]{"cancelevent", "ce"}, false);
      this.forceAsPower = mlc.getBoolean(new String[]{"forceaspower", "fap"}, true);
       Utils.mythicmobs.getSkillManager().queueSecondPass(() -> {
         if (this.onTradeSkillName != null) {
            this.onTradeSkill = Utils.mythicmobs.getSkillManager().getSkill(this.onTradeSkillName);
         }

      });
   }

   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      new OnTrade.Tracker(data, target);
      return SkillResult.SUCCESS;
   }

   private class Tracker extends Aura.AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillMetadata data, AbstractEntity entity) {
         super(entity, data);
         this.start();
      }

      public void auraStart() {
         this.registerAuraComponent(Events.subscribe(InventoryClickEvent.class).filter((event) -> {
        	if ( event.getInventory().getHolder()!=null && ((Entity) event.getInventory().getHolder()).getType().equals(EntityType.VILLAGER) ) {
        		oldXP = ( (Villager) event.getInventory().getHolder()).getVillagerExperience();
                return ((Entity) event.getInventory().getHolder()).getUniqueId().equals(this.entity.get().getUniqueId());
        	}
        	return false;
         }).handler((event) -> {
        	 if ( event.getInventory().getHolder()!=null && ((Entity) event.getInventory().getHolder()).getType().equals(EntityType.VILLAGER) ) {
	        	 Schedulers.sync().runLater(() -> {
	        		 newXP = ( (Villager) event.getInventory().getHolder()).getVillagerExperience();
	        		 
	        		 if (newXP!=oldXP) {
		        		 SkillMetadata meta = this.skillMetadata.deepClone();
		                 meta.setEntityTarget(BukkitAdapter.adapt(((Entity) event.getInventory().getHolder())));
		                 if (this.executeAuraSkill(OnTrade.this.onTradeSkill, meta)) {
		                    if (OnTrade.this.cancelEvent) {
		                       event.setCancelled(true);
		                    }
		                 };
	        		 }
	              }, 1L);
        	 }

         }));
         this.executeAuraSkill(OnTrade.this.onStartSkill, this.skillMetadata);
      }
   }
}
