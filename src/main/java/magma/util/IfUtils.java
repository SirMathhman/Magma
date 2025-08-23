package magma.util;

import magma.core.CompileException;
import java.util.Optional;

public final class IfUtils {
  private IfUtils() {
  }

  public static final class IfParts {
    public final int condStart;
    public final int condEnd;
    public final int thenStart;
    public final int thenEnd;
    public final int elseStart;
    public final int elseEnd;

    public IfParts(int condStart, int condEnd, int thenStart, int thenEnd, int elseStart, int elseEnd) {
      this.condStart = condStart;
      this.condEnd = condEnd;
      this.thenStart = thenStart;
      this.thenEnd = thenEnd;
      this.elseStart = elseStart;
      this.elseEnd = elseEnd;
    }
  }

  public static Optional<IfParts> tryFindIfParts(String s, int idx) throws CompileException {
    if (!s.startsWith("if", idx))
      return Optional.empty();
    int afterIf = idx + 2;
    // find '(' after if
    int open = s.indexOf('(', afterIf);
    if (open == -1)
      return Optional.empty();
    int close = s.indexOf(')', open + 1);
    if (close == -1)
      throw new CompileException("Unterminated '(' in if-condition at index " + idx + " in expression: '" + s + "'");
    int condStart = open + 1;
    int condEnd = close;
    // find then block
    int thenOpen = s.indexOf('{', close + 1);
    if (thenOpen == -1)
      throw new CompileException("Expected '{' after if condition in expression: '" + s + "'");
    int thenEnd = BlockUtils.findClosingBrace(s, thenOpen);
    if (thenEnd == -1)
      throw new CompileException(
          "Unterminated then-block in if-expression starting at index " + thenOpen + " in expression: '" + s + "'");
    // find else
    int elseIdx = s.indexOf("else", thenEnd);
    if (elseIdx == -1)
      throw new CompileException("Expected 'else' after if-then block in expression: '" + s + "'");
    int elseOpen = s.indexOf('{', elseIdx + 4);
    if (elseOpen == -1)
      throw new CompileException("Expected '{' after else in expression: '" + s + "'");
    int elseEnd = BlockUtils.findClosingBrace(s, elseOpen);
    if (elseEnd == -1)
      throw new CompileException(
          "Unterminated else-block in if-expression starting at index " + elseOpen + " in expression: '" + s + "'");
    return Optional.of(new IfParts(condStart, condEnd, thenOpen, thenEnd, elseOpen, elseEnd));
  }
}
