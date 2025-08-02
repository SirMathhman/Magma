package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

final class Main {
	private Main() {}

	public static void main(final String[] args) {
		try {
			Files.writeString(Paths.get(".", "src", "windows", "magma", "Main.c"), "");
		} catch (final IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}