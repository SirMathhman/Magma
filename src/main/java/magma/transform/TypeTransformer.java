package magma.transform;

import magma.compile.Lang;

import java.util.ArrayList;
import java.util.List;

public class TypeTransformer {
	static Lang.CType transformType(Lang.JType type) {
		return switch (type) {
			case Lang.Invalid invalid -> invalid;
			case Lang.JGeneric generic -> {
				// Convert Function<T, R> to function pointer R (*)(T)
				final List<Lang.JType> listOption = generic.typeArguments().orElse(new ArrayList<>());
				if (generic.base().equals("Function") && listOption.size() == 2) {
					final Lang.CType paramType = transformType(listOption.get(0));
					final Lang.CType returnType = transformType(listOption.get(1));
					yield new Lang.FunctionPointer(returnType, List.of(paramType));
				}
				yield new Lang.CGeneric(generic.base(), listOption.stream().map(TypeTransformer::transformType).toList());
			}
			case Lang.Array array -> {
				Lang.CType childType = transformType(array.child());
				yield new Lang.Pointer(childType);
			}
			case Lang.Identifier identifier -> {
				if (identifier.value().equals("String")) yield new Lang.Pointer(new Lang.Identifier("char"));
				yield identifier;
			}
			case Lang.Wildcard wildcard -> new Lang.Invalid("???");
			case Lang.Variadic variadic -> new Lang.Invalid("???");
		};
	}
}
