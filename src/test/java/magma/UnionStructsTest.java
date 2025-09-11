package magma;

import org.junit.jupiter.api.Test;

public class UnionStructsTest {

    @Test
    public void unionStructsIsCheck() {
        TestUtils.assertValid("struct Ok { } struct Err { } type Result = Ok | Err; let a : Result = Ok { }; a is Ok", "true");
        TestUtils.assertValid("struct Ok { } struct Err { } type Result = Ok | Err; let b : Result = Err { }; b is Err", "true");
    }
}
