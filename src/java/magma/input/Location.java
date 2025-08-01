package magma.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a location in the source code, consisting of package segments and a name.
 * For example, the package segments could be [com, microsoft], and the name could be "Test".
 */
public class Location {
	private final List<String> packageSegments;
	private final String name;

	/**
	 * Creates a new Location with the given package segments and name.
	 *
	 * @param packageSegments the list of package segments
	 * @param name            the name
	 */
	public Location(List<String> packageSegments, String name) {
		this.packageSegments = packageSegments != null ? Collections.unmodifiableList(new ArrayList<>(packageSegments))
																									 : Collections.emptyList(); this.name = name != null ? name : "";
	}

	/**
	 * Creates a new Location from a fully qualified name.
	 * The last segment is considered the name, and all preceding segments are package segments.
	 *
	 * @param fullyQualifiedName the fully qualified name (e.g., "com.microsoft.Test")
	 * @return a new Location
	 */
	public static Location fromFullyQualifiedName(String fullyQualifiedName) {
		if (fullyQualifiedName == null || fullyQualifiedName.isEmpty()) {
			return new Location(Collections.emptyList(), "");
		}

		String[] segments = fullyQualifiedName.split("\\."); if (segments.length == 1) {
			return new Location(Collections.emptyList(), segments[0]);
		}

		List<String> packageSegments = new ArrayList<>(); for (int i = 0; i < segments.length - 1; i++) {
			packageSegments.add(segments[i]);
		}

		return new Location(packageSegments, segments[segments.length - 1]);
	}

	/**
	 * Creates a new Location from a simple name with no package.
	 *
	 * @param name the name
	 * @return a new Location
	 */
	public static Location fromName(String name) {
		return new Location(Collections.emptyList(), name);
	}

	/**
	 * Gets the package segments.
	 *
	 * @return an unmodifiable list of package segments
	 */
	public List<String> getPackageSegments() {
		return packageSegments;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the fully qualified name, which is the package segments joined with dots, followed by the name.
	 *
	 * @return the fully qualified name
	 */
	public String getFullyQualifiedName() {
		if (packageSegments.isEmpty()) {
			return name;
		}

		StringBuilder sb = new StringBuilder(); for (String segment : packageSegments) {
			sb.append(segment).append(".");
		} sb.append(name);

		return sb.toString();
	}

	/**
	 * Returns a string representation of this location.
	 *
	 * @return a string representation
	 */
	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	/**
	 * Checks if this location is equal to another object.
	 *
	 * @param obj the object to compare with
	 * @return true if the objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true; if (obj == null || getClass() != obj.getClass()) return false;

		Location location = (Location) obj;
		return Objects.equals(packageSegments, location.packageSegments) && Objects.equals(name, location.name);
	}

	/**
	 * Returns a hash code for this location.
	 *
	 * @return a hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(packageSegments, name);
	}
}