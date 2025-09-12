package magma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Small helper utilities for combining multiple Result attempts into a
 * single Result that returns the first Ok or a compound InterpretError
 * containing all attempted errors as children.
 */
public final class ResultUtils {
    private ResultUtils() {
    }

    /**
     * Given a list of Optional<Result<T, InterpretError>> attempts, return:
     * - Optional.empty() if no attempt was applicable (all optionals empty)
     * - the first Ok(Result.Ok) wrapped in Optional if any attempt succeeded
     * - Optional.of(Result.Err(compound)) if attempts were applicable but all
     *   produced errors. The compound InterpretError will contain each
     *   attempted error as a child for diagnostics.
     */
    public static <T> Optional<Result<T, InterpretError>> firstOkOrCompound(
            List<Optional<Result<T, InterpretError>>> attempts, String reason, String source) {
        List<Result.Err<T, InterpretError>> errs = new ArrayList<>();
        boolean anyPresent = false;
        for (Optional<Result<T, InterpretError>> opt : attempts) {
            if (opt.isEmpty())
                continue;
            anyPresent = true;
            Result<T, InterpretError> r = opt.get();
            if (r instanceof Result.Ok)
                return Optional.of(r);
            errs.add((Result.Err<T, InterpretError>) r);
        }
        if (!anyPresent)
            return Optional.empty();
        // All applicable attempts failed -> build compound InterpretError
        List<InterpretError> children = new ArrayList<>();
        for (Result.Err<T, InterpretError> e : errs) {
            children.add(e.error());
        }
        InterpretError compound = new InterpretError(reason, source, List.copyOf(children));
        return Optional.of(new Result.Err<>(compound));
    }
}
