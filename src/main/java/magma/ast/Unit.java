package magma.ast;

import magma.parser.Location;

public record Unit(Location location, String extension, String input) {
}
