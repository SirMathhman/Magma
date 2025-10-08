package magma.transform;

import magma.Tuple;
import magma.compile.CNodes;
import magma.compile.JNodes;
import magma.compile.Lang;
import magma.compile.error.CompileError;
import magma.compile.rule.RootTokenSequence;
import magma.compile.rule.TokenSequence;
import magma.list.ArrayList;
import magma.list.Collections;
import magma.list.List;
import magma.list.NonEmptyList;
import magma.list.NonEmptyListCollector;
import magma.list.Stream;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Ok;
import magma.result.Result;

public class Transformer {
	private static final TokenSequence INVALID_MARKER = new RootTokenSequence("???");
	private static final TokenSequence EMPTY_ROOT_TOKEN_SEQUENCE = new RootTokenSequence("");

	public static Lang.CFunction transformMethod(Lang.JMethod method, TokenSequence structName) {
		final Option<NonEmptyList<Lang.JDefinition>> maybeOldParams = method.params();

		final Option<NonEmptyList<Lang.CParameter>> newParams =
				maybeOldParams.flatMap(params -> NonEmptyList.fromList(params.stream()
																																		 .map(Transformer::transformParameter)
																																		 .toList()));

		final Lang.CDefinition cDefinition = transformDefinition(method.definition());

		// Extract type parameters from method signature
		final Option<NonEmptyList<Lang.Identifier>> extractedTypeParams = extractMethodTypeParameters(method);

		// Convert method body from Option<NonEmptyList<JMethodSegment>> to
		// NonEmptyList<CFunctionSegment>
		// JMethodSegment and CFunctionSegment share the same implementations
		// (Placeholder, Whitespace, Invalid)
		final NonEmptyList<Lang.CFunctionSegment> bodySegments = method.body()
																																	 .map(Transformer::getCFunctionSegmentNonEmptyList)
																																	 .orElse(NonEmptyList.of(new Lang.Invalid(
																																			 INVALID_MARKER)));
		final TokenSequence name = new RootTokenSequence(cDefinition.name().value() + "_" + structName.value());
		return new Lang.CFunction(new Lang.CDefinition(name, cDefinition.type(), cDefinition.typeParameters()),
															newParams,
															bodySegments,
															new Some<TokenSequence>(new RootTokenSequence(System.lineSeparator())),
															extractedTypeParams);
	}

	private static NonEmptyList<Lang.CFunctionSegment> getCFunctionSegmentNonEmptyList(NonEmptyList<Lang.JMethodSegment> body) {
		return body.stream()
							 .map(Transformer::transformFunctionSegment)
							 .collect(new NonEmptyListCollector<Lang.CFunctionSegment>())
							 .orElse(NonEmptyList.of(new Lang.Invalid(INVALID_MARKER)));
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
			default -> new Lang.Invalid(INVALID_MARKER);
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
		return new Lang.CBlock(jBlock.children()
																 .stream()
																 .map(Transformer::transformFunctionSegment)
																 .collect(new NonEmptyListCollector<Lang.CFunctionSegment>()));
	}

	private static Lang.CIf transformIf(Lang.JIf anIf) {
		final Lang.CFunctionSegment body = transformFunctionSegment(anIf.body());
		final Lang.CBlock record;
		if (body instanceof Lang.CBlock b) record = b;
		else record = new Lang.CBlock(new Some<NonEmptyList<Lang.CFunctionSegment>>(NonEmptyList.of(body)));

		return new Lang.CIf(transformExpression(anIf.condition()), record);
	}

	private static Lang.CInvocation handleConstruction(Lang.JConstruction jConstruction) {
		String name = "new_" + transformType(jConstruction.type()).toTokens();
		final Option<NonEmptyList<Lang.CExpression>> list =
				jConstruction.arguments().flatMap(Transformer::transformExpressionList);

		return new Lang.CInvocation(new Lang.Identifier(new RootTokenSequence(name)), list);
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
			case Lang.JString jString -> new Lang.CString(jString.content().orElse(EMPTY_ROOT_TOKEN_SEQUENCE));
			case Lang.JEquals jEquals ->
					new Lang.CEquals(transformExpression(jEquals.left()), transformExpression(jEquals.right()));
			case Lang.And and -> new Lang.CAnd(transformExpression(and.left()), transformExpression(and.right()));
			case Lang.CharNode charNode -> charNode;
			case JNodes.JCast cast -> new CNodes.Cast(transformType(cast.type()), transformExpression(cast.child()));
			case Lang.Index index -> new Lang.Invalid(INVALID_MARKER);
			case Lang.InstanceOf instanceOf -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.JGreaterThan jGreaterThan -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.JGreaterThanEquals jGreaterThanEquals -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.JLessThan jLessThan -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.JLessThanEquals jLessThanEquals -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.JNotEquals jNotEquals -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.JOr jOr -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.JSubtract jSubtract -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.Lambda lambda -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.MethodAccess methodAccess -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.NewArray newArray -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.Not not -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.NumberNode numberNode -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.Quantity quantity -> new Lang.Invalid(new RootTokenSequence("???"));
			case Lang.SwitchExpr switchExpr -> new Lang.Invalid(new RootTokenSequence("???"));
		};
	}

	private static Lang.CInvocation transformInvocation(Lang.JInvocation jInvocation) {
		final Option<NonEmptyList<Lang.CExpression>> newArguments =
				jInvocation.arguments().flatMap(Transformer::transformExpressionList);

		return new Lang.CInvocation(transformExpression(jInvocation.caller()), newArguments);
	}

	private static Option<NonEmptyList<Lang.CExpression>> transformExpressionList(NonEmptyList<Lang.JExpression> list) {
		return list.stream().map(Transformer::transformExpression).collect(new NonEmptyListCollector<Lang.CExpression>());
	}

	private static Lang.CParameter transformParameter(Lang.JDefinition param) {
		final CNodes.CType transformedType = transformType(param.type());

		// If the transformed type is a FunctionPointer, create
		// CFunctionPointerDefinition
		if (transformedType instanceof CNodes.CFunctionPointer(CNodes.CType returnType, List<CNodes.CType> paramTypes))
			return new Lang.CFunctionPointerDefinition(param.name(), returnType, paramTypes);

		// Otherwise create regular CDefinition
		return new Lang.CDefinition(param.name(), transformedType, new None<List<Lang.Identifier>>());
	}

	private static Option<NonEmptyList<Lang.Identifier>> extractMethodTypeParameters(Lang.JMethod method) {
		// Analyze method signature to detect generic type parameters
		final List<String> typeVars = new ArrayList<String>();

		// Check return type for type variables
		collectTypeVariables(method.definition().type(), typeVars);

		// Check parameter types for type variables
		if (method.params() instanceof Some<NonEmptyList<Lang.JDefinition>>(NonEmptyList<Lang.JDefinition> paramList))
			paramList.stream().forEach(param -> collectTypeVariables(param.type(), typeVars));

		if (typeVars.isEmpty()) return new None<NonEmptyList<Lang.Identifier>>();

		// Convert to Identifier objects
		return typeVars.stream().map(s -> new Lang.Identifier(new RootTokenSequence(s))).collect(NonEmptyList.collector());
	}

	private static void collectTypeVariables(Lang.JType type, List<String> typeVars) {
		switch (type) {
			case Lang.Identifier ident -> {
				// Single letter identifiers are likely type variables (R, E, etc.)
				String identValue = ident.value().value();
				if (identValue.length() == 1 && Character.isUpperCase(identValue.charAt(0))) typeVars.addLast(identValue);
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
																																													TokenSequence name) {
		return switch (self) {
			case Lang.Invalid invalid ->
					new Tuple<List<Lang.CRootSegment>, Option<Lang.CDefinition>>(List.of(invalid), new None<Lang.CDefinition>());
			case Lang.JMethod method ->
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

		final TokenSequence name = aClass.name();

		// Collect tuples for each child once (avoids re-evaluating and allows immutable
		// construction)
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

		final Lang.CStructure structure = new Lang.CStructure(name,
																													fields,
																													new Some<TokenSequence>(new RootTokenSequence(System.lineSeparator())),
																													aClass.typeParameters());

		// Build resulting root segments list: structure followed by flattened child
		// segments

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

	static CNodes.CType transformType(Lang.JType type) {
		return switch (type) {
			case Lang.Invalid invalid -> invalid;
			case Lang.JGeneric generic -> transformGeneric(generic);
			case Lang.Array array -> transformArray(array);
			case Lang.Identifier identifier -> transformIdentifier(identifier);
			case Lang.JQualified qualified -> new Lang.Identifier(qualified.last());
			case Lang.Variadic variadic -> new Lang.Invalid(new RootTokenSequence(type.toString()));
			case Lang.Wildcard wildcard -> new Lang.Invalid(new RootTokenSequence(type.toString()));
		};
	}

	private static CNodes.CType transformIdentifier(Lang.Identifier identifier) {
		final TokenSequence value = identifier.value();
		if (value.equalsSlice("String")) return new Lang.Pointer(new Lang.Identifier(new RootTokenSequence("char")));
		return identifier;
	}

	private static Lang.Pointer transformArray(Lang.Array array) {
		CNodes.CType childType = transformType(array.child());
		return new Lang.Pointer(childType);
	}

	private static CNodes.CType transformGeneric(Lang.JGeneric generic) {
		// Convert Function<T, R> to function pointer R (*)(T)
		final List<Lang.JType> listOption = generic.typeArguments().orElse(new ArrayList<Lang.JType>());
		if (generic.base().endsWith("Function") && listOption.size() == 2) {
			final CNodes.CType paramType = transformType(listOption.get(0).orElse(null));
			final CNodes.CType returnType = transformType(listOption.get(1).orElse(null));
			return new CNodes.CFunctionPointer(returnType, List.of(paramType));
		}

		// Transform type arguments to CType list
		final List<CNodes.CType> transformedTypes = listOption.stream().map(Transformer::transformType).toList();

		// Create NonEmptyList from the transformed types
		// If the list is empty, this is an error case - generics should always have
		// type arguments
		return NonEmptyList.fromList(transformedTypes)
											 .map(nonEmptyTypes -> (CNodes.CType) new Lang.CTemplate(generic.base().last(),
																																							 nonEmptyTypes))
											 .orElse(new Lang.Invalid(new RootTokenSequence(
													 "Empty type arguments for generic " + generic.base().last()), new None<TokenSequence>()));
	}
}
