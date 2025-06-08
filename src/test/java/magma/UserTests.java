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

    @Test
    void test3() {
        assertTranspile("""
                public class Temp {
                    void temp() {
                        var x = 100;
                        var y = x;
                    }
                }""", """
                export default class Temp {
                    temp(): void {
                        let x : number = 100;
                        let y : number = x;
                    }
                }""");
    }

    @Test
    void test4() {
        assertTranspile("""
                public class Temp {
                    void empty() {
                        var value = getValue();
                    }
                    int getValue() {
                        return 100;
                    }
                }""", """
                export default class Temp {
                    empty(): void {
                        let value : number = getValue();
                    }
                    getValue(): number {
                        return 100;
                    }
                }""");
    }

    @Test
    void test5() {
        assertTranspile("""
                public class Temp {
                    void empty() {
                        var value = new Temp().getValue();
                    }
                    int getValue() {
                        return 100;
                    }
                }""", """
                export default class Temp {
                    empty(): void {
                        let value : number = new Temp().getValue();
                    }
                    getValue(): number {
                        return 100;
                    }
                }""");
    }

    @Test
    void test6() {
        assertTranspile("""
            public interface PathLike {
                PathLike resolve(String other);
            }""", """
            export interface PathLike {
                resolve(other : string): PathLike;
            }""");
    }

    @Test
    void actualAnnotationShouldProduceNoContent() {
        assertTranspile("""
            @Actual
            public class PathLike {
            }""", "");
    }
}
