import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Application {
	public static void main(String[] args) {
		try {
			final var output = Compiler.compile(Files.readString(Paths.get(".", "index.mgs")));
			Files.writeString(Paths.get(".", "index.c"), output);
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
