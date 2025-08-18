import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {
  /**
   * Run the application: pass input to Compiler.compile and write result to a
   * temp .c file.
   * 
   * @param input input string
   * @return the path to the created temp file
   * @throws IOException on write error
   */
  public Path run(String input) throws IOException {
    String compiled = Compiler.compile(input);
    Path tmp = Files.createTempFile("magma_", ".c");
    Files.write(tmp, compiled.getBytes(StandardCharsets.UTF_8));
    return tmp;
  }
}
