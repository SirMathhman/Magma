package org.example;

// Lightweight per-run state to signal function returns without exceptions.
final class ReturnState {
	boolean active;
	String value;
}
