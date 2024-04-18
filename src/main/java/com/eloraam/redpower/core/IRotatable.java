package com.eloraam.redpower.core;

public abstract interface IRotatable {
	public abstract int getPartMaxRotation(int paramInt, boolean paramBoolean);

	public abstract int getPartRotation(int paramInt, boolean paramBoolean);

	public abstract void setPartRotation(int paramInt1, boolean paramBoolean, int paramInt2);
}
