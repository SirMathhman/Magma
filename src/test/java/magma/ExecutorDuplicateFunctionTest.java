package magma;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import magma.Result.Ok;
import magma.Result.Err;

public class ExecutorDuplicateFunctionTest {

    @Test
    void defining_two_functions_with_same_name_should_error() {
        String input = "fn first() : I32 => 100; fn first() : I32 => 100;";

        switch (Executor.execute(input)) {
            case Ok(var v) -> fail("expected Err but got Ok: " + v);
            case Err(var err) -> assertNotNull(err);
        }
    }
}
