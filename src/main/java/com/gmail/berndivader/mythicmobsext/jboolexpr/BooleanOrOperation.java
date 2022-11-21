package com.gmail.berndivader.mythicmobsext.jboolexpr;

final class BooleanOrOperation implements IBoolean {

	private IBoolean iBoolean1;

	private IBoolean iBoolean2;

	BooleanOrOperation(final IBoolean newIBoolean1, final IBoolean newIBoolean2) {
		if (newIBoolean1 == null) {
			throw new IllegalArgumentException("newIBoolean1 is null");
		}
		this.iBoolean1 = newIBoolean1;
		if (newIBoolean2 == null) {
			throw new IllegalArgumentException("newIBoolean2 is null");
		}
		this.iBoolean2 = newIBoolean2;

	}

	@Override
	public boolean booleanValue() {
		return (this.iBoolean1.booleanValue() || this.iBoolean2.booleanValue());
	}

	@Override
	public String toString() {
		return "(" +
				this.iBoolean1 +
				"||" +
				this.iBoolean2 +
				")";
	}
}
