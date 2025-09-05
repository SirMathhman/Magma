import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) {
    Path p = Paths.get("src", "main", "magma", "Index.mgs");
    String src = "";
    try {
      src = Files.readString(p);
    } catch (Exception e) {
      System.err.println("Failed to read " + p + ": " + e.getMessage());
      System.exit(2);
    }

    Interpreter interp = new Interpreter();
    Result<String, InterpretError> res = interp.interpret(src, "");

    if (res instanceof Result.Ok) {
      @SuppressWarnings("unchecked")
      Result.Ok<String, InterpretError> ok = (Result.Ok<String, InterpretError>) res;
      System.out.println(ok.value());
      System.exit(0);
    }
    if (res instanceof Result.Err) {
      @SuppressWarnings("unchecked")
      Result.Err<String, InterpretError> err = (Result.Err<String, InterpretError>) res;
      System.err.println("Error: " + err.error());
      System.exit(1);
    }

    System.err.println("Unknown interpreter result");
    System.exit(3);
  }
}
