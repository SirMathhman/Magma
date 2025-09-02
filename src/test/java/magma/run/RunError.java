package magma.run;

import magma.diagnostics.Error_;

import java.util.Optional;

public record RunError(String message, Optional<Error_> maybeCause, Optional<String> generatedOutput)
    implements Error_ {
  public RunError(String message) {
    this(message, Optional.empty(), Optional.empty());
  }

  public RunError(String message, Optional<Error_> maybeCause) {
    this(message, maybeCause, Optional.empty());
  }

  public RunError(String message, Optional<Error_> maybeCause, String generatedOutput) {
    this(message, maybeCause, Optional.ofNullable(generatedOutput));
  }
}
