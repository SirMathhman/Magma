public class Runner {
  public String run(String input) {
    Location location = new Location(java.util.Collections.emptyList(), "");
    Unit unit = new Unit(location, ".mgs", input);
    java.util.Set<Unit> units = java.util.Collections.singleton(unit);
    Compiler compiler = new Compiler();
    java.util.Set<Unit> compiledUnits = compiler.compile(units);

    // Write units to a temporary directory using NIO
    try {
      java.nio.file.Path tempDir = java.nio.file.Files.createTempDirectory("units");
      for (Unit u : compiledUnits) {
        Location loc = u.location();
        // Build the directory path from namespace
        java.nio.file.Path dir = tempDir;
        for (String ns : loc.namespace()) {
          dir = dir.resolve(ns);
        }
        java.nio.file.Files.createDirectories(dir);
        // File name: name + extension
        String fileName = loc.name() + u.extension();
        java.nio.file.Path filePath = dir.resolve(fileName);
        java.nio.file.Files.writeString(filePath, u.content());

      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to write units", e);
    }
    return input;
  }
}
