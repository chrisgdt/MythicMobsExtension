package com.gmail.berndivader.mythicmobsext.targeters;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.gmail.berndivader.mythicmobsext.conditions.ThreatTable;
import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;

@ExternalAnnotation(name = "ttt,threattabletargeter", author = "BerndiVader")
public class ThreatTableTargeter extends ISelectorEntity {
	RangedDouble r1, r2;
	static Field f1;

	static {
		try {
			f1 = ThreatTable.class.getDeclaredField("threatTable");
			f1.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			// Auto-generated catch block
		}
	}

	public ThreatTableTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
		r1 = new RangedDouble(mlc.getString("range", ">0"));
		r2 = new RangedDouble(mlc.getString("threat", ">0"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity> targets = new HashSet<>();
		List<Entry<AbstractEntity, Double>> tt = null;
		ActiveMob am = (ActiveMob) data.getCaster();
		if (am != null && am.hasThreatTable()) {
			try {
				if ((tt = sort((ConcurrentHashMap<AbstractEntity, Double>) f1.get(am.getThreatTable()))) == null)
					return targets;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if (tt != null) {
				int i2 = 1;
				for (int i1 = tt.size(); i1 >= 0; i1--) {
					Entry<AbstractEntity, Double> p1 = tt.get(i1);
					if (r1.equals(i2) && r2.equals(p1.getValue()))
						targets.add(p1.getKey());
					i2++;
				}
			}
		}
		return this.applyOffsets(targets);
	}

	static List<Entry<AbstractEntity, Double>> sort(Map<AbstractEntity, Double> tt) {
		List<Entry<AbstractEntity, Double>> l1 = new ArrayList<>(tt.entrySet());
		Collections.sort(l1, Entry.comparingByValue());
		Collections.reverse(l1);
		return l1;
	}

}
