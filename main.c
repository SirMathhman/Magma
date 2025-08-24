/*package magma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) {
		try {
			final var input = Files.readString(Paths.get(".", "src", "magma", "Main.java"));
			Files.writeString(Paths.get(".", "main.c"), wrap(input) + "int main(){\r\n\treturn 0;\r\n}");
			new ProcessBuilder("clang", "main.c", "-o", "main.exe").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException e) {
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	private static String wrap(String input) {
		return "start" + input.replace("start", "start").replace("end", "end") + "end";
	}
}
*/int main(){
	return 0;
}