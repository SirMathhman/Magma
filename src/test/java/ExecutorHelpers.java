public class ExecutorHelpers {
  public static Result<String, RunError> okFromOutput(String runOutput) {
    String out = Util.trimTrailingNewlines(runOutput);
    return new Ok<>(out);
  }

  public static Result<String, RunError> errFromException(Exception e) {
    return new Err<>(new RunError("Failed to build or execute units: " + e.getMessage()));
  }

  public static java.util.List<java.nio.file.Path> filterFilesByExt(java.util.List<java.nio.file.Path> files, String... exts) {
    java.util.List<java.nio.file.Path> out = new java.util.ArrayList<>();
    for (java.nio.file.Path p : files) {
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
