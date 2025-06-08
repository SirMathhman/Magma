package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerImportTest {

    @Test
    void translatesImportsRelativeToPackage() {
        var javaSrc = String.join(System.lineSeparator(),
            "package com.example.util;",
            "",
            "import com.example.data.Model;",
            "",
            "public class Util {}"
        );

        var expected = String.join(System.lineSeparator(),
            "import Model from \"../data/Model\";",
            "export default class Util {}"
        );

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void importsFromSamePackageUseDotSlash() {
        var javaSrc = String.join(System.lineSeparator(),
            "package com.example;",
            "",
            "import com.example.Helper;",
            "",
            "public class Foo {}"
        );

        var expected = String.join(System.lineSeparator(),
            "import Helper from \"./Helper\";",
            "export default class Foo {}"
        );

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void addsImportsForReferencedClasses() {
        var javaSrc = String.join(System.lineSeparator(),
            "package com.example;",
            "",
            "public class B {",
            "    void empty() {",
            "        var v = A.value;",
            "    }",
            "}");

        var expected = String.join(System.lineSeparator(),
            "import A from \"./A\";",
            "export default class B {",
            "    empty(): void {",
            "        let v : unknown = A.value;",
            "    }",
            "}");

        var result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
