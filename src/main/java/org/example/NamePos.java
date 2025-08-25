package org.example;

// Small container for a parsed name and the index after it (spaces skipped)
final class NamePos {
	final String name;
	final int after;

	NamePos(String n, int a) {
		this.name = n;
		this.after = a;
	}
}
