package com.kkinder.charmap;

public class IndexEntry {

	public final int index;
	public final CharSequence value;

	public IndexEntry(int index, CharSequence value) {
		this.index = index;
		this.value = value;
	}

	@Override
	public String toString() {
		return value == null ? null : value.toString();
	}
}