package org.example;

import java.util.List;

public final class FunctionInfo {
	final List<String> paramNames;
	final String bodyValue; // currently only stores parsed value's string

	FunctionInfo(List<String> paramNames, String bodyValue) {
		this.paramNames = paramNames;
		this.bodyValue = bodyValue;
	}
}
