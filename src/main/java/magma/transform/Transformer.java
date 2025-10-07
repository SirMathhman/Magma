package magma.transform;

import magma.Tuple;
import magma.compile.CLang;
import magma.compile.Lang;
import magma.compile.error.CompileError;
import magma.list.ArrayList;
import magma.list.Collections;
import magma.list.List;
import magma.list.Stream;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;

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
			case Some<List<Lang.JMethodSegment>>(List<Lang.JMethodSegment> segments) ->
					segments.stream().map(Transformer::transformFunctionSegment).toList();
		};

		return new Lang.Function(new Lang.CDefinition(cDefinition.name() + "_" + structName,
																									cDefinition.type(),
																									cDefinition.typeParameters()),
														 newParams,
														 bodySegments,
														 new Some<String>(System.lineSeparator()),
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
			default -> new Lang.Invalid("???");
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
		String name = "new_" + transformType(jConstruction.type()).stringify();
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
		final List<Lang.CExpression> newArguments = jInvocation.arguments()
																													 .orElse(new ArrayList<Lang.JExpression>())
																													 .stream()
																													 .map(Transformer::transformExpression)
																													 .toList();
		return new Lang.CInvocation(transformExpression(jInvocation.caller()), newArguments);
	}

	private static Lang.CParameter transformParameter(Lang.JDefinition param) {
		final CLang.CType transformedType = transformType(param.type());

		// If the transformed type is a FunctionPointer, create
		// CFunctionPointerDefinition
		if (transformedType instanceof CLang.CFunctionPointer(CLang.CType returnType, List<CLang.CType> paramTypes))
			return new Lang.CFunctionPointerDefinition(param.name(), returnType, paramTypes);

		// Otherwise create regular CDefinition
		return new Lang.CDefinition(param.name(), transformedType, new None<List<Lang.Identifier>>());
	}

	private static Option<List<Lang.Identifier>> extractMethodTypeParameters(Lang.Method method) {
		// Analyze method signature to detect generic type parameters
		final List<String> typeVars = new ArrayList<String>();

		// Check return type for type variables
		collectTypeVariables(method.definition().type(), typeVars);

		// Check parameter types for type variables
		if (method.params() instanceof Some<List<Lang.JDefinition>>(List<Lang.JDefinition> paramList))
			paramList.stream().forEach(param -> collectTypeVariables(param.type(), typeVars));

		if (typeVars.isEmpty()) return new None<List<Lang.Identifier>>();

		// Convert to Identifier objects
		final List<Lang.Identifier> identifiers = typeVars.stream().map(Lang.Identifier::new).toList();

		return new Some<List<Lang.Identifier>>(identifiers);
	}

	private static void collectTypeVariables(Lang.JType type, List<String> typeVars) {
		switch (type) {
			case Lang.Identifier ident -> {
				// Single letter identifiers are likely type variables (R, E, etc.)
				if (ident.value().length() == 1 && Character.isUpperCase(ident.value().charAt(0)))
					typeVars.addLast(ident.value());
			}
			case Lang.JGeneric generic -> {
				// Check base type name for type variables
				// Collect from type typeArguments
				final List<Lang.JType> listOption = generic.typeArguments().orElse(new ArrayList<Lang.JType>());
				listOption.stream().forEach(arg -> collectTypeVariables(arg, typeVars));
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
		return new Lang.CDefinition(definition.name(), transformType(definition.type()), typeParams);
	}

	static List<Lang.CRootSegment> flattenRootSegment(Lang.JavaRootSegment segment) {
		return switch (segment) {
			case Lang.JStructure jStructure -> flattenStructure(jStructure);
			case Lang.Invalid invalid -> List.of(invalid);
			default -> Collections.emptyList();
		};
	}

	public static Result<Lang.CRoot, CompileError> transform(Lang.JRoot node) {
		final List<Lang.JavaRootSegment> children = node.children();
		final Stream<Lang.JavaRootSegment> stream = children.stream();
		final Stream<List<Lang.CRootSegment>> listStream = stream.map(Transformer::flattenRootSegment);
		final Stream<Lang.CRootSegment> cRootSegmentStream = listStream.flatMap(List::stream);
		final List<Lang.CRootSegment> newChildren = cRootSegmentStream.toList();
		return new Ok<Lang.CRoot, CompileError>(new Lang.CRoot(newChildren));
	}

	static Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>> flattenStructureSegment(Lang.JStructureSegment self,
																																													String name) {
		return switch (self) {
			case Lang.Invalid invalid ->
					new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(List.of(invalid), new None<Lang.CDefinition>());
			case Lang.Method method ->
					new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(List.of(transformMethod(method, name)),
																																			 new None<Lang.CDefinition>());
			case Lang.JStructure jClass ->
					new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(flattenStructure(jClass),
																																			 new None<Lang.CDefinition>());
			case Lang.Field field -> new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(Collections.emptyList(),
																																														new Some<Lang.CDefinition>(
																																																transformDefinition(
																																																		field.value())));
			case Lang.JInitialization jInitialization -> new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(
					Collections.emptyList(),
					new Some<Lang.CDefinition>(transformDefinition(jInitialization.definition())));
			case Lang.JDefinition jDefinition ->
					new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(Collections.emptyList(),
																																			 new Some<Lang.CDefinition>(transformDefinition(
																																					 jDefinition)));
			default -> new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(Collections.emptyList(),
																																							new None<Lang.CDefinition>());
		};
	}

	static List<Lang.CRootSegment> flattenStructure(Lang.JStructure aClass) {
		final List<Lang.JStructureSegment> children = aClass.children();

		// Special handling for Record params - add them as struct fields
		final List<Lang.CDefinition> recordFields = extractRecordParamsAsFields(aClass).copy();

		final String name = aClass.name();

		// Collect tuples for each child once (avoids re-evaluating and allows immutable construction)
		final List<Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>> tuples =
				children.stream().map(child -> flattenStructureSegment(child, name)).toList();

		// Flatten all CRoootSegments produced by the children
		final List<Lang.CRootSegment> segments = tuples.stream().map(Tuple::left).flatMap(List::stream).toList();

		// Collect any field definitions returned by children
		final List<Lang.CDefinition> additionalFields = tuples.stream()
																													.map(Tuple::right)
																													.filter(opt -> opt instanceof Some<Lang.CDefinition>)
																													.map(opt -> ((Some<Lang.CDefinition>) opt).value())
																													.toList();

		// Combine record fields and additional fields immutably
		final List<Lang.CDefinition> fields = recordFields.addAll(additionalFields);

		final Lang.Structure structure =
				new Lang.Structure(name, fields, new Some<String>(System.lineSeparator()), aClass.typeParameters());

		// Build resulting root segments list: structure followed by flattened child segments

		return new ArrayList<Lang.CRootSegment>().addLast(structure).addAll(segments);
	}

	private static List<Lang.CDefinition> extractRecordParamsAsFields(Lang.JStructure structure) {
		if (structure instanceof Lang.RecordNode record) {
			Option<List<Lang.JDefinition>> params = record.params();
			if (params instanceof Some<List<Lang.JDefinition>>(List<Lang.JDefinition> paramList))
				return paramList.stream().map(Transformer::transformDefinition).toList();
		}
		return Collections.emptyList();
	}

	static CLang.CType transformType(Lang.JType type) {
		return switch (type) {
			case Lang.Invalid invalid -> invalid;
			case Lang.JGeneric generic -> transformGeneric(generic);
			case Lang.Array array -> transformArray(array);
			case Lang.Identifier identifier -> transformIdentifier(identifier);
			default -> new Lang.Invalid("???");
		};
	}

	private static CLang.CType transformIdentifier(Lang.Identifier identifier) {
		if (identifier.value().equals("String")) return new Lang.Pointer(new Lang.Identifier("char"));
		return identifier;
	}

	private static Lang.Pointer transformArray(Lang.Array array) {
		CLang.CType childType = transformType(array.child());
		return new Lang.Pointer(childType);
	}

	private static CLang.CType transformGeneric(Lang.JGeneric generic) {
		// Convert Function<T, R> to function pointer R (*)(T)
		final List<Lang.JType> listOption = generic.typeArguments().orElse(new ArrayList<Lang.JType>());
		if (generic.base().endsWith("Function") && listOption.size() == 2) {
			final CLang.CType paramType = transformType(listOption.get(0).orElse(null));
			final CLang.CType returnType = transformType(listOption.get(1).orElse(null));
			return new CLang.CFunctionPointer(returnType, List.of(paramType));
		}
		return new Lang.CTemplate(generic.base().last(), listOption.stream().map(Transformer::transformType).toList());
	}
}
