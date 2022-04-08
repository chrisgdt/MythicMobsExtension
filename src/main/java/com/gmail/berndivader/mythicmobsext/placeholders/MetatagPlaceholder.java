package com.gmail.berndivader.mythicmobsext.placeholders;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.skills.placeholders.types.MetaPlaceholder;

import java.util.function.BiFunction;

public class MetatagPlaceholder implements IPlaceHolder<PlaceholderMeta> {
	String placeholder_name;
	static final String error = "#NOTFOUND>";

	public MetatagPlaceholder() {
	}

	void register() {
		Utils.mythicmobs.getPlaceholderManager().register(placeholder_name, new MetaPlaceholder(this.transformer()));
	}

	@Override
	public BiFunction<PlaceholderMeta, String, String> transformer() {
		return null;
	}

}
