public class ApplicationException extends java.io.IOException {
  private final int exitCode;
  private final String output;

  public ApplicationException(String message, int exitCode, String output) {
    super(message + ": exitCode=" + exitCode + ", output=" + output);
    this.exitCode = exitCode;
    this.output = output;
  }

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
    this.exitCode = -1;
    this.output = null;
  }

  public ApplicationException(String message) {
    super(message);
    this.exitCode = -1;
    this.output = null;
  }

  public int getExitCode() {
    return exitCode;
  }

  public String getOutput() {
    return output;
  }
}
