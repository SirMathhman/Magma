package magma.run;

import magma.util.Err;
import magma.util.Ok;
import magma.util.Result;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExecutorHelpers {
  public static Result<String, RunError> okFromOutput(String runOutput) {
    String out = Util.trimTrailingNewlines(runOutput);
    return new Ok<>(out);
  }

  public static Result<String, RunError> errFromException(Exception e) {
    return new Err<>(new RunError("Failed to build or execute units: " + e.getMessage()));
  }

  public static List<Path> filterFilesByExt(List<Path> files, String... exts) {
    List<Path> out = new ArrayList<>();
    for (Path p : files) {
      String s = p.toString();
      for (String e : exts) {
        if (s.endsWith(e)) {
          out.add(p);
          break;
        }
      }
    }
    return out;
  }
}
