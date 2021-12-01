package com.gmail.berndivader.mythicmobsext.items;

public enum WhereEnum {
	HAND, OFFHAND, HELMET, CHESTPLATE, BOOTS, LEGGINGS, ARMOR, INVENTORY, ANY, SLOT, BACKBAG, TAG;

	public static WhereEnum getWhere(String s) {
		if (s == null)
			return null;
		try {
			return WhereEnum.valueOf(s.toUpperCase());
		} catch (Exception ex) {
			return WhereEnum.ANY;
		}
	}
}
