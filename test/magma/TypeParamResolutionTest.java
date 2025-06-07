package magma;

import magma.ast.*;
import magma.compile.*;
import magma.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TypeParamResolutionTest {
    @Test
    void typeParamsInRecordParametersAreResolved() {
        TypeParam typeParam = new TypeParam("T");
        TypeParamSet typeParams = new TypeParamSet(Lists.of(typeParam));
        CompileState state = new CompileState().enter(new StructureFrame(typeParams));
        Type type = Main.parseType("T", state);
        assertTrue(type instanceof TypeParam, "expected type param to be resolved");
    }
}
