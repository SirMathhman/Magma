package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final Path target = Paths.get(".", "src", "main", "windows", "magma", "Main.c");
			final Path targetParent = target.getParent();
			if (!Files.exists(targetParent)) Files.createDirectories(targetParent);
			Files.writeString(target, "int main(){" + System.lineSeparator() + "\treturn 0;" + System.lineSeparator() + "}");
		} catch (IOException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}
}
