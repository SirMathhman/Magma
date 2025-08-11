package magma;

import org.junit.jupiter.api.Test;

class ExternTest extends CompilerTestBase {
    @Test
    void simpleExtern() {
        assertValid("extern fn getMyValue() : I32;", "");
    }
    
    @Test
    void externWithGeneric() {
        assertValid("extern fn printf<Length: USize>(format : *CStr, args : [Any; Length]) : Void;", "");
    }
}