package magma.run;

import magma.Error_;

import java.util.Optional;

public record RunError(String message, Optional<Error_> maybeCause) implements Error_ {
  public RunError(String message) {
    this(message, Optional.empty());
  }
}
