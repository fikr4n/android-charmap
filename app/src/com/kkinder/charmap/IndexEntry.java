package com.kkinder.charmap;

public class IndexEntry<T> {

	public final int index;
	public final T value;

	public IndexEntry(int index, T value) {
		this.index = index;
		this.value = value;
	}

	@Override
	public String toString() {
		return value == null ? null : value.toString();
	}
}