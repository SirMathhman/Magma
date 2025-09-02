package magma.feature;

import org.junit.jupiter.api.Test;

import static magma.TestUtils.assertAllInvalid;
import static magma.TestUtils.assertAllValidWithPrelude;

public class TypeAliasTest {
    @Test
    void alias() {
        assertAllValidWithPrelude("type Simple = I32; let value : Simple = readInt(); value", "10", "10");
    }

    @Test
    void isOperator() {
        assertAllValidWithPrelude("type Simple = I32; let value : Simple = readInt(); value is I32", "10", "true");
    }

    @Test
    void twoTypesTrue() {
        assertAllValidWithPrelude("type Simple = I32 | Bool; let value : Simple = 100; value is I32", "", "true");
    }

    @Test
    void twoTypesFalse() {
        assertAllValidWithPrelude("type Simple = I32 | Bool; let value : Simple = 100; value is Bool", "", "false");
    }

    @Test
    void typeAmbiguous() {
        assertAllValidWithPrelude(
                "type Simple = I32 | Bool; let value : Simple = if (readInt() == 100) 10 else true; value is I32",
                "100", "10");
    }

    @Test
    void aliasMultiple() {
        assertAllValidWithPrelude("type One = I32; type Two = One; let x : Two = readInt(); x", "100", "100");
    }

    @Test
    void unionHasStructAsVariant() {
        assertAllValidWithPrelude(
                "struct Wrapper {field : I32} type Simple = I32 | Wrapper; let value : Simple = Wrapper { readInt() }; value is Wrapper",
                "100", "true");
    }

    @Test
    void letBeforeType() {
        assertAllValidWithPrelude("let x : I32 = readInt(); type Temp = I32; let y : Temp = x; y", "100", "100");
    }

    @Test
    void aliasRequiresSemicolon() {
        assertAllInvalid("type temp = I32");
    }
}