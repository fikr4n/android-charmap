package com.kkinder.charmap;

/**
 * A pair of index (integer) and value (CharSequence) which the `toString()` returns the value.
 * This class is used in filtering of list adapter so the index is not missed.
 * 
 * @author fikr4n
 *
 */
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