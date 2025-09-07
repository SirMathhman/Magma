package com.example.magma.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a Java type location: package parts and a class name.
 *
 * Note: the field is named {@code packageParts} because {@code package}
 * is a Java keyword and cannot be used as an identifier.
 */
public record Location(List<String> packageParts, String className) {
	/**
	 * Return the package as a dot-separated string (empty string for the default
	 * package).
	 */
	public String packageAsString() {
		return packageParts.isEmpty() ? "" : String.join(".", packageParts);
	}

	@Override
	public String toString() {
		String pkg = packageAsString();
		return pkg.isEmpty() ? className : pkg + "." + className;
	}
}
