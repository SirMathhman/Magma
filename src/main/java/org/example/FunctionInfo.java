package org.example;

import java.util.List;

public final class FunctionInfo {
	final String name; // null for anonymous closures
	final List<String> paramNames;
	final String bodyValue; // currently only stores parsed value's string

	FunctionInfo(String name, List<String> paramNames, String bodyValue) {
		this.name = name;
		this.paramNames = paramNames;
		this.bodyValue = bodyValue;
	}
}
