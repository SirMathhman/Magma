public class ApplicationException extends Exception {
  private final int exitCode;
  private final String output;

  public ApplicationException(String message, int exitCode, String output) {
    super(message + ": exitCode=" + exitCode + ", output=" + output);
    this.exitCode = exitCode;
    this.output = output;
  }

  public int getExitCode() {
    return exitCode;
  }

  public String getOutput() {
    return output;
  }
}
