package com.gmail.berndivader.mythicmobsext.mechanics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableType;

@ExternalAnnotation(name="fileline", author="Seyarada")
public class FileLine extends VariableMechanic implements ITargetedEntitySkill {
   PlaceholderString value;
   VariableType type;
   File fileOrigin;
   int line;
   String fileName;

   public FileLine(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
       super(manager, file, skill, mlc);
      
      fileName = mlc.getString(new String[] { "file", "f"}, "");
      fileOrigin = new File(Main.getPlugin().getDataFolder().getPath() + "/files/" + fileName);
      line = mlc.getInteger(new String[] { "line", "l"}, -1);

      String strType = mlc.getString(new String[]{"type", "t"}, VariableType.INTEGER.toString());

      try {
         this.type = VariableType.valueOf(strType.toUpperCase());
      } catch (Exception e) {
         MythicLogger.errorMechanicConfig(this, mlc, "'" + strType + "' is not a valid variable type.");
      }

   }

   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
	   String result = null;
	   int size = 0;
	   Path path = Paths.get(Main.getPlugin().getDataFolder().getPath() + "/files/" + fileName);
	   int y = Integer.valueOf(line);
	   VariableRegistry variables = getVariableManager().getRegistry(this.scope, data, target);
	   
	   try {
		   Stream<String> lines = Files.lines(path);
		   size = (int) Files.lines(path).count();
		   if (y < 0) y = ThreadLocalRandom.current().nextInt(1, size + 1);
		   
		   result = lines.skip(y-1).findFirst().get();
		   lines.close();
		} catch (IOException e) {e.printStackTrace();}
      
      if (variables == null) {
         MythicLogger.errorMechanicConfig(this, this.config, "Failed to get variable registry");
         return SkillResult.CONDITION_FAILED;
        
      } else {
         Variable var = null;
         
         if (this.type != VariableType.INTEGER && this.type != VariableType.FLOAT) {
            var = Variable.ofType(this.type, result, this.duration);
            
         } else if (this.type == VariableType.INTEGER) {
            var = Variable.ofType(this.type, Integer.valueOf(result), this.duration);
            
         } else if (this.type == VariableType.FLOAT) {
             var = Variable.ofType(this.type, Float.valueOf(result), this.duration);
         }

         variables.put(this.key.get(), var);
         return SkillResult.SUCCESS;
      }
   }
}