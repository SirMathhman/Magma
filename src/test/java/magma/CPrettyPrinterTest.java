package magma;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CPrettyPrinterTest {
    @Test
    void prettyPrintIncludeWithFunction() {
        String compactC = "#include <stdio.h> int32_t main(){printf(\"%s\", \"Hello World!\"); return 0;}";
        String expected = "#include <stdio.h>\n\nint32_t main() {\n    printf(\"%s\", \"Hello World!\");\n    return 0;\n}";
        String actual = CPrettyPrinter.prettyPrint(compactC);
        assertEquals(expected, actual);
    }
}