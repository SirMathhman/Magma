/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "java", "magma", "Main.java"));

			final var targetParent = Paths.get(".", "src", "windows", "magma");
			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);

			final var target = targetParent.resolve("Main.c");
			Files.writeString(target, "start" + input.replace("start", "start").replace("end", "end") + "end");
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
*/