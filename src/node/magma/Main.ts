/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {
		final var sourceDirectory = Paths.get(".", "src", "java");
		try (final var stream = Files.walk(sourceDirectory)) {
			final var sources = stream.filter(Files::isRegularFile)
																.filter(path -> path.toString().endsWith(".java"))
																.collect(Collectors.toSet());

			for (var source : sources) {
				final var relative = sourceDirectory.relativize(source);
				final var relativeParent = relative.getParent();
				final var fileName = relative.getFileName().toString();
				final var extensionSeparator = fileName.lastIndexOf(".");
				if (extensionSeparator >= 0) {
					final var name = fileName.substring(0, extensionSeparator);
					final var targetDirectory = Paths.get(".", "src", "node");
					final var targetParent = targetDirectory.resolve(relativeParent);
					if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

					final var target = targetParent.resolve(name + ".ts");
					Files.writeString(target, "/*" + Files.readString(source) + "*/");
				}
			}
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
*/