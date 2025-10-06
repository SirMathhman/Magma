package magma.transform;

import magma.compile.Lang;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Transformer {

	public static Lang.Function transformMethod(Lang.Method method, String structName) {
		final List<Lang.JDefinition> oldParams = switch (method.params()) {
			case None<List<Lang.JDefinition>> _ -> Collections.emptyList();
			case Some<List<Lang.JDefinition>> v -> v.value();
		};

		final List<Lang.CParameter> newParams = oldParams.stream().map(Transformer::transformParameter).toList();

		final Lang.CDefinition cDefinition = transformDefinition(method.definition());

		// Extract type parameters from method signature
		final Option<List<Lang.Identifier>> extractedTypeParams = extractMethodTypeParameters(method);

		// Convert method body from Option<List<JFunctionSegment>> to
		// List<CFunctionSegment>
		// JFunctionSegment and CFunctionSegment share the same implementations
		// (Placeholder, Whitespace, Invalid)
		final List<Lang.CFunctionSegment> bodySegments = switch (method.body()) {
			case None<List<Lang.JMethodSegment>> _ -> Collections.emptyList();
			case Some<List<Lang.JMethodSegment>>(List<Lang.JMethodSegment> segments) -> {
				yield segments.stream().map(Transformer::transformFunctionSegment).toList();
			}
		};

		return new Lang.Function(new Lang.CDefinition(cDefinition.name() + "_" + structName,
																									cDefinition.type(),
																									cDefinition.typeParameters()),
														 newParams,
														 bodySegments,
														 new Some<>(System.lineSeparator()),
														 extractedTypeParams);
	}

	static Lang.CFunctionSegment transformFunctionSegment(Lang.JMethodSegment segment) {
		return switch (segment) {
			case Lang.JIf anIf -> transformIf(anIf);
			case Lang.Invalid invalid -> invalid;
			case Lang.Placeholder placeholder -> placeholder;
			case Lang.Whitespace whitespace -> whitespace;
			case Lang.JReturn aReturn -> new Lang.CReturn(transformExpression(aReturn.value()));
			case Lang.LineComment lineComment -> lineComment;
			case Lang.JBlock jBlock -> transformBlock(jBlock);
			case Lang.JInitialization jInitialization -> transformInitialization(jInitialization);
			case Lang.JAssignment jAssignment -> transformAssignment(jAssignment);
			case Lang.JPostFix jPostFix -> new Lang.CPostFix(transformExpression(jPostFix.value()));
			case Lang.JElse jElse -> new Lang.CElse(transformFunctionSegment(jElse.child()));
			case Lang.Break aBreak -> aBreak;
			case Lang.JWhile jWhile -> transformWhile(jWhile);
			case Lang.JInvocation invocation -> transformInvocation(invocation);
			case Lang.JConstruction jConstruction -> handleConstruction(jConstruction);
			case Lang.JDefinition jDefinition -> transformDefinition(jDefinition);
			case Lang.Catch aCatch -> new Lang.Invalid("???");
			case Lang.Try aTry -> new Lang.Invalid("???");
			case Lang.SwitchStatement switchStatement -> new Lang.Invalid("???");
			case Lang.Yield yield -> new Lang.Invalid("???");
		};
	}

	private static Lang.CWhile transformWhile(Lang.JWhile jWhile) {
		return new Lang.CWhile(transformExpression(jWhile.condition()), transformFunctionSegment(jWhile.body()));
	}

	private static Lang.CAssignment transformAssignment(Lang.JAssignment jAssignment) {
		return new Lang.CAssignment(transformExpression(jAssignment.location()), transformExpression(jAssignment.value()));
	}

	private static Lang.CInitialization transformInitialization(Lang.JInitialization jInitialization) {
		return new Lang.CInitialization(transformDefinition(jInitialization.definition()),
																		transformExpression(jInitialization.value()));
	}

	private static Lang.CBlock transformBlock(Lang.JBlock jBlock) {
		return new Lang.CBlock(jBlock.children().stream().map(Transformer::transformFunctionSegment).toList());
	}

	private static Lang.CIf transformIf(Lang.JIf anIf) {
		return new Lang.CIf(transformExpression(anIf.condition()), transformFunctionSegment(anIf.body()));
	}

	private static Lang.CInvocation handleConstruction(Lang.JConstruction jConstruction) {
		String name = "new_" + TypeTransformer.transformType(jConstruction.type()).stringify();
		final List<Lang.CExpression> list = jConstruction.arguments()
																										 .orElse(new ArrayList<Lang.JExpression>())
																										 .stream()
																										 .map(Transformer::transformExpression)
																										 .toList();
		return new Lang.CInvocation(new Lang.Identifier(name), list);
	}

	static Lang.CExpression transformExpression(Lang.JExpression expression) {
		return switch (expression) {
			case Lang.Invalid invalid -> invalid;
			case Lang.Identifier identifier -> identifier;
			case Lang.JFieldAccess fieldAccess ->
					new Lang.CFieldAccess(transformExpression(fieldAccess.child()), fieldAccess.name());
			case Lang.JInvocation jInvocation -> transformInvocation(jInvocation);
			case Lang.JConstruction jConstruction -> handleConstruction(jConstruction);
			case Lang.JAdd add -> new Lang.CAdd(transformExpression(add.left()), transformExpression(add.right()));
			case Lang.JString jString -> new Lang.CString(jString.content().orElse(""));
			case Lang.JEquals jEquals ->
					new Lang.CEquals(transformExpression(jEquals.left()), transformExpression(jEquals.right()));
			case Lang.And and -> new Lang.CAnd(transformExpression(and.left()), transformExpression(and.right()));
			case Lang.CharNode charNode -> charNode;
			default -> new Lang.Invalid("???");
		};
	}

	private static Lang.CInvocation transformInvocation(Lang.JInvocation jInvocation) {
		final List<Lang.CExpression> newArguments =
				jInvocation.arguments().orElse(new ArrayList<>()).stream().map(Transformer::transformExpression).toList();
		return new Lang.CInvocation(transformExpression(jInvocation.caller()), newArguments);
	}

	private static Lang.CParameter transformParameter(Lang.JDefinition param) {
		final Lang.CType transformedType = TypeTransformer.transformType(param.type());

		// If the transformed type is a FunctionPointer, create
		// CFunctionPointerDefinition
		if (transformedType instanceof Lang.FunctionPointer(Lang.CType returnType, List<Lang.CType> paramTypes))
			return new Lang.CFunctionPointerDefinition(param.name(), returnType, paramTypes);

		// Otherwise create regular CDefinition
		return new Lang.CDefinition(param.name(), transformedType, new None<>());
	}

	private static Option<List<Lang.Identifier>> extractMethodTypeParameters(Lang.Method method) {
		// Analyze method signature to detect generic type parameters
		final Set<String> typeVars = new HashSet<>();

		// Check return type for type variables
		collectTypeVariables(method.definition().type(), typeVars);

		// Check parameter types for type variables
		if (method.params() instanceof Some<List<Lang.JDefinition>>(List<Lang.JDefinition> paramList))
			paramList.forEach(param -> collectTypeVariables(param.type(), typeVars));

		if (typeVars.isEmpty()) return new None<>();

		// Convert to Identifier objects
		final List<Lang.Identifier> identifiers = typeVars.stream().map(Lang.Identifier::new).toList();

		return new Some<>(identifiers);
	}

	private static void collectTypeVariables(Lang.JType type, Set<String> typeVars) {
		switch (type) {
			case Lang.Identifier ident -> {
				// Single letter identifiers are likely type variables (R, E, etc.)
				if (ident.value().length() == 1 && Character.isUpperCase(ident.value().charAt(0))) typeVars.add(ident.value());
			}
			case Lang.JGeneric generic -> {
				// Check base type name for type variables
				if (generic.base().length() == 1 && Character.isUpperCase(generic.base().charAt(0)))
					typeVars.add(generic.base());
				// Collect from type typeArguments
				final List<Lang.JType> listOption = generic.typeArguments().orElse(new ArrayList<>());
				listOption.forEach(arg -> collectTypeVariables(arg, typeVars));
			}
			case Lang.Array array -> collectTypeVariables(array.child(), typeVars);
			default -> {
				/* Other types don't contain type variables */
			}
		}
	}

	public static Lang.CDefinition transformDefinition(Lang.JDefinition definition) {
		// Default to no type parameters for backward compatibility
		final Option<List<Lang.Identifier>> typeParams = definition.typeParameters();
		return new Lang.CDefinition(definition.name(), TypeTransformer.transformType(definition.type()), typeParams);
	}
}
