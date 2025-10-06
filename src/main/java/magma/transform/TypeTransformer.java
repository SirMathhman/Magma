package magma.transform;

import magma.compile.Lang;

import java.util.ArrayList;
import java.util.List;

public class TypeTransformer {
	static Lang.CType transformType(Lang.JType type) {
		return switch (type) {
			case Lang.Invalid invalid -> invalid;
			case Lang.JGeneric generic -> transformGeneric(generic);
			case Lang.Array array -> transformArray(array);
			case Lang.Identifier identifier -> transformIdentifier(identifier);
			default -> new Lang.Invalid("???");
		};
	}

	private static Lang.CType transformIdentifier(Lang.Identifier identifier) {
		if (identifier.value().equals("String")) return new Lang.Pointer(new Lang.Identifier("char"));
		return identifier;
	}

	private static Lang.Pointer transformArray(Lang.Array array) {
		Lang.CType childType = transformType(array.child());
		return new Lang.Pointer(childType);
	}

	private static Lang.CType transformGeneric(Lang.JGeneric generic) {
		// Convert Function<T, R> to function pointer R (*)(T)
		final List<Lang.JType> listOption = generic.typeArguments().orElse(new ArrayList<>());
		if (generic.base().equals("Function") && listOption.size() == 2) {
			final Lang.CType paramType = transformType(listOption.get(0));
			final Lang.CType returnType = transformType(listOption.get(1));
			return new Lang.FunctionPointer(returnType, List.of(paramType));
		}
		return new Lang.CGeneric(generic.base(), listOption.stream().map(TypeTransformer::transformType).toList());
	}
}
