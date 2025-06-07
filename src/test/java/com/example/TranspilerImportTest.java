package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.app.Transpiler;
import org.junit.jupiter.api.Test;

class TranspilerImportTest {

    @Test
    void translatesImportsRelativeToPackage() {
        String javaSrc = String.join("\n",
            "package com.example.util;",
            "",
            "import com.example.data.Model;",
            "",
            "public class Util {}"
        );

        String expected = String.join("\n",
            "import Model from \"../data/Model\";",
            "export default class Util {}"
        );

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }

    @Test
    void importsFromSamePackageUseDotSlash() {
        String javaSrc = String.join("\n",
            "package com.example;",
            "",
            "import com.example.Helper;",
            "",
            "public class Foo {}"
        );

        String expected = String.join("\n",
            "import Helper from \"./Helper\";",
            "export default class Foo {}"
        );

        String result = new Transpiler().toTypeScript(javaSrc);
        assertEquals(expected, result);
    }
}
