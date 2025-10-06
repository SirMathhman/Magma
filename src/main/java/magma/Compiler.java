package magma;

import magma.compile.Lang;
import magma.compile.Serializers;
import magma.compile.error.CompileError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static magma.compile.Lang.*;

public class Compiler {
	public static Result<String, CompileError> compile(String input) {
		return JRoot().lex(input)
									.flatMap(node -> Serializers.deserialize(JRoot.class, node))
									.flatMap(Compiler::transform)
									.flatMap(cRoot -> Serializers.serialize(Lang.CRoot.class, cRoot))
									.flatMap(CRoot()::generate);
	}

	public static Result<CRoot, CompileError> transform(JRoot node) {
		final List<JavaRootSegment> children = node.children();
		final Stream<JavaRootSegment> stream = children.stream();
		final Stream<List<CRootSegment>> listStream = stream.map(Compiler::flattenRootSegment);
		final Stream<CRootSegment> cRootSegmentStream = listStream.flatMap(Collection::stream);
		final List<CRootSegment> newChildren = cRootSegmentStream.toList();
		return new Ok<>(new CRoot(newChildren));
	}

	private static List<CRootSegment> flattenRootSegment(JavaRootSegment segment) {
		return switch (segment) {
			case JStructure jStructure -> flattenStructure(jStructure);
			case Invalid invalid -> List.of(invalid);
			default -> Collections.emptyList();
		};
	}

	private static List<CRootSegment> flattenStructure(JStructure aClass) {
		final List<JStructureSegment> children = aClass.children();

		final ArrayList<CRootSegment> segments = new ArrayList<>();
		final ArrayList<CDefinition> fields = new ArrayList<>();

		// Special handling for Record params - add them as struct fields
		addRecordParamsAsFields(aClass, fields);

		final String name = aClass.name();
		children.stream().map(child -> flattenStructureSegment(child, name)).forEach(tuple -> {
			segments.addAll(tuple.left());
			if (tuple.right() instanceof Some<CDefinition>(CDefinition value)) fields.add(value);
		});

		final Structure structure =
				new Structure(name, fields, new Some<>(System.lineSeparator()), aClass.typeParameters());
		final List<CRootSegment> copy = new ArrayList<>();
		copy.add(structure);
		copy.addAll(segments);
		return copy;
	}

	private static void addRecordParamsAsFields(JStructure aClass, ArrayList<CDefinition> fields) {
		if (aClass instanceof RecordNode record) {
			Option<List<JDefinition>> params = record.params();
			if (params instanceof Some<List<JDefinition>>(List<JDefinition> paramList))
				paramList.stream().map(Compiler::transformDefinition).forEach(fields::add);
		}
	}

	private static Tuple<List<CRootSegment>, Option<CDefinition>> flattenStructureSegment(JStructureSegment self,
																																												String name) {
		return switch (self) {
			case Invalid invalid -> new Tuple<>(List.of(invalid), new None<>());
			case Method method -> new Tuple<>(List.of(transformMethod(method, name)), new None<>());
			case JStructure jClass -> new Tuple<>(flattenStructure(jClass), new None<>());
			case Field field -> new Tuple<>(Collections.emptyList(), new Some<>(transformDefinition(field.value())));
			case Whitespace _, LineComment _, BlockComment _ ->
					new Tuple<>(Collections.emptyList(), new None<>());
			case JInitialization jInitialization ->
					new Tuple<>(Collections.emptyList(), new Some<>(transformDefinition(jInitialization.definition())));
			case JDefinition jDefinition ->
					new Tuple<>(Collections.emptyList(), new Some<>(transformDefinition(jDefinition)));
		};
	}

	private static Function transformMethod(Method method, String structName) {
		final List<JDefinition> oldParams = switch (method.params()) {
			case None<List<JDefinition>> _ -> Collections.emptyList();
			case Some<List<JDefinition>> v -> v.value();
		};

		final List<CParameter> newParams = oldParams.stream().map(Compiler::transformParameter).toList();

		final CDefinition cDefinition = transformDefinition(method.definition());

		// Extract type parameters from method signature
		final Option<List<Identifier>> extractedTypeParams = extractMethodTypeParameters(method);

		// Convert method body from Option<List<JFunctionSegment>> to
		// List<CFunctionSegment>
		// JFunctionSegment and CFunctionSegment share the same implementations
		// (Placeholder, Whitespace, Invalid)
		final List<CFunctionSegment> bodySegments = switch (method.body()) {
			case None<List<JMethodSegment>> _ -> Collections.emptyList();
			case Some<List<JMethodSegment>>(List<JMethodSegment> segments) -> {
				yield segments.stream().map(Compiler::transformFunctionSegment).toList();
			}
		};

		return new Function(new CDefinition(cDefinition.name() + "_" + structName,
																									cDefinition.type(),
																									cDefinition.typeParameters()),
														 newParams,
														 bodySegments,
														 new Some<>(System.lineSeparator()),
														 extractedTypeParams);
	}

	static CFunctionSegment transformFunctionSegment(JMethodSegment segment) {
		return switch (segment) {
			case JIf anIf -> transformIf(anIf);
			case Invalid invalid -> invalid;
			case Placeholder placeholder -> placeholder;
			case Whitespace whitespace -> whitespace;
			case JReturn aReturn -> new CReturn(transformExpression(aReturn.value()));
			case LineComment lineComment -> lineComment;
			case JBlock jBlock -> transformBlock(jBlock);
			case JInitialization jInitialization -> transformInitialization(jInitialization);
			case JAssignment jAssignment -> transformAssignment(jAssignment);
			case JPostFix jPostFix -> new CPostFix(transformExpression(jPostFix.value()));
			case JElse jElse -> new CElse(transformFunctionSegment(jElse.child()));
			case Break aBreak -> aBreak;
			case JWhile jWhile -> transformWhile(jWhile);
			case JInvocation invocation -> transformInvocation(invocation);
			case JConstruction jConstruction -> handleConstruction(jConstruction);
			case JDefinition jDefinition -> transformDefinition(jDefinition);
			case Catch aCatch -> new Invalid("???");
			case Try aTry -> new Invalid("???");
			case SwitchStatement switchStatement -> new Invalid("???");
			case Yield yield -> new Invalid("???");
		};
	}

	private static CWhile transformWhile(JWhile jWhile) {
		return new CWhile(transformExpression(jWhile.condition()), transformFunctionSegment(jWhile.body()));
	}

	private static CAssignment transformAssignment(JAssignment jAssignment) {
		return new CAssignment(transformExpression(jAssignment.location()), transformExpression(jAssignment.value()));
	}

	private static CInitialization transformInitialization(JInitialization jInitialization) {
		return new CInitialization(transformDefinition(jInitialization.definition()),
																		transformExpression(jInitialization.value()));
	}

	private static CBlock transformBlock(JBlock jBlock) {
		return new CBlock(jBlock.children().stream().map(Compiler::transformFunctionSegment).toList());
	}

	private static CIf transformIf(JIf anIf) {
		return new CIf(transformExpression(anIf.condition()), transformFunctionSegment(anIf.body()));
	}

	private static CInvocation handleConstruction(JConstruction jConstruction) {
		String name = "new_" + transformType(jConstruction.type()).stringify();
		final List<CExpression> list = jConstruction.arguments()
																								.orElse(new ArrayList<JExpression>())
																								.stream()
																								.map(Compiler::transformExpression)
																								.toList();
		return new CInvocation(new Identifier(name), list);
	}

	static CExpression transformExpression(JExpression expression) {
		return switch (expression) {
			case Invalid invalid -> invalid;
			case Identifier identifier -> identifier;
			case JFieldAccess fieldAccess ->
					new CFieldAccess(transformExpression(fieldAccess.child()), fieldAccess.name());
			case JInvocation jInvocation -> transformInvocation(jInvocation);
			case JConstruction jConstruction -> handleConstruction(jConstruction);
			case JAdd add -> new CAdd(transformExpression(add.left()), transformExpression(add.right()));
			case JString jString -> new CString(jString.content().orElse(""));
			case JEquals jEquals ->
					new CEquals(transformExpression(jEquals.left()), transformExpression(jEquals.right()));
			case And and -> new CAnd(transformExpression(and.left()), transformExpression(and.right()));
			case CharNode charNode -> charNode;
			default -> new Invalid("???");
		};
	}

	private static CInvocation transformInvocation(JInvocation jInvocation) {
		final List<CExpression> newArguments =
				jInvocation.arguments().orElse(new ArrayList<>()).stream().map(Compiler::transformExpression).toList();
		return new CInvocation(transformExpression(jInvocation.caller()), newArguments);
	}

	private static CParameter transformParameter(JDefinition param) {
		final CType transformedType = transformType(param.type());

		// If the transformed type is a FunctionPointer, create
		// CFunctionPointerDefinition
		if (transformedType instanceof FunctionPointer(CType returnType, List<CType> paramTypes))
			return new CFunctionPointerDefinition(param.name(), returnType, paramTypes);

		// Otherwise create regular CDefinition
		return new CDefinition(param.name(), transformedType, new None<>());
	}

	private static Option<List<Identifier>> extractMethodTypeParameters(Method method) {
		// Analyze method signature to detect generic type parameters
		final Set<String> typeVars = new HashSet<>();

		// Check return type for type variables
		collectTypeVariables(method.definition().type(), typeVars);

		// Check parameter types for type variables
		if (method.params() instanceof Some<List<JDefinition>>(List<JDefinition> paramList))
			paramList.forEach(param -> collectTypeVariables(param.type(), typeVars));

		if (typeVars.isEmpty()) return new None<>();

		// Convert to Identifier objects
		final List<Identifier> identifiers = typeVars.stream().map(Identifier::new).toList();

		return new Some<>(identifiers);
	}

	private static void collectTypeVariables(JType type, Set<String> typeVars) {
		switch (type) {
			case Identifier ident -> {
				// Single letter identifiers are likely type variables (R, E, etc.)
				if (ident.value().length() == 1 && Character.isUpperCase(ident.value().charAt(0))) typeVars.add(ident.value());
			}
			case JGeneric generic -> {
				// Check base type name for type variables
				if (generic.base().length() == 1 && Character.isUpperCase(generic.base().charAt(0)))
					typeVars.add(generic.base());
				// Collect from type typeArguments
				final List<JType> listOption = generic.typeArguments().orElse(new ArrayList<>());
				listOption.forEach(arg -> collectTypeVariables(arg, typeVars));
			}
			case Array array -> collectTypeVariables(array.child(), typeVars);
			default -> {
				/* Other types don't contain type variables */
			}
		}
	}

	private static CDefinition transformDefinition(JDefinition definition) {
		// Default to no type parameters for backward compatibility
		final Option<List<Identifier>> typeParams = definition.typeParameters();
		return new CDefinition(definition.name(), transformType(definition.type()), typeParams);
	}

	private static CType transformType(JType type) {
		return switch (type) {
			case Invalid invalid -> invalid;
			case JGeneric generic -> {
				// Convert Function<T, R> to function pointer R (*)(T)
				final List<JType> listOption = generic.typeArguments().orElse(new ArrayList<>());
				if (generic.base().equals("Function") && listOption.size() == 2) {
					final CType paramType = transformType(listOption.get(0));
					final CType returnType = transformType(listOption.get(1));
					yield new FunctionPointer(returnType, List.of(paramType));
				}
				yield new CGeneric(generic.base(), listOption.stream().map(Compiler::transformType).toList());
			}
			case Array array -> {
				CType childType = transformType(array.child());
				yield new Pointer(childType);
			}
			case Identifier identifier -> {
				if (identifier.value().equals("String")) yield new Pointer(new Identifier("char"));
				yield identifier;
			}
			case Wildcard wildcard -> new Invalid("???");
			case Variadic variadic -> new Invalid("???");
		};
	}
}
