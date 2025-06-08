package magma;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTests {
    private void assertTranspile(String javaSrc, String expected) {
        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void test0() {
        assertTranspile("""
                public class Foo {
                    void copy(int src) {
                        return fold(new None<String>(), () -> {
                        });
                    }
                }""", """
                export default class Foo {
                    copy(src: number): void {
                        return fold(new None<String>(), () => {
                        });
                    }
                }""");
    }

    @Test
    void test1() {
        assertTranspile("""
                public class Foo {
                    void copy(int src) {
                        return (String value) -> {
                        };
                    }
                }""", """
                export default class Foo {
                    copy(src: number): void {
                        return (value : string) => {
                        };
                    }
                }""");
    }


    @Test
    void test2() {
        assertTranspile("""
                public class Foo {
                    void copy(int src) {
                        var temp = new Foo();
                    }
                }""", """
                export default class Foo {
                    copy(src: number): void {
                        let temp : Foo = new Foo();
                    }
                }""");
    }
}
