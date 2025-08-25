package org.example;

import java.util.ArrayList;

// Compact container for parsed closure header
final class ClosureHeader {
	final ArrayList<String> params;
	final int beforeBody;

	ClosureHeader(ArrayList<String> params, int beforeBody) {
		this.params = params;
		this.beforeBody = beforeBody;
	}
}
