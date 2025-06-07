package magma;

import magma.ast.*;
import magma.compile.*;
import magma.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecordTypeParamTest {
    @Test
    void parameterInRecordUsesTypeParam() {
        TypeParam typeParam = new TypeParam("T");
        TypeParamSet typeParams = new TypeParamSet(Lists.of(typeParam));
        CompileState state = new CompileState().enter(new StructureFrame(typeParams));
        Parameter param = Main.parseParameter("T value", state);
        assertTrue(param instanceof Definition);
        Definition def = (Definition) param;
        assertTrue(def.type instanceof TypeParam, "expected type param to resolve in parameter");
    }
}
