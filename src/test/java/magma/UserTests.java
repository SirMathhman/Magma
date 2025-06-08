package magma;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTests {

    @Test
    void test1() {
        var javaSrc = """
                public class Foo {
                    void copy(int src) {
                        return (String value) -> {
                        };
                    }
                }""";

        var expected = """
                export default class Foo {
                    copy(src: number): void {
                        return (value : string) => {
                        };
                    }
                }""";

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void test0() {
        var javaSrc = """
                public class Foo {
                    void copy(int src) {
                        return fold(new None<String>(), () -> {
                        });
                    }
                }""";

        var expected = """
                export default class Foo {
                    copy(src: number): void {
                        return fold(new None<String>(), () => {
                        });
                    }
                }""";

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
