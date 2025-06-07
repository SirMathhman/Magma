package magma.compile;

import magma.util.*;
import magma.ast.*;
public record StructurePrototype(String modifiers, String name, TypeParamSet typeParams,
                          List<Type> implementsTypes, Option<List<Definition>> maybeFields,
                          String beforeBody, String inputBody) {
}
