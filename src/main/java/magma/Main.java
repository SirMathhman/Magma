package magma;

import magma.compile.Lang;
import magma.compile.Serializers;
import magma.compile.error.ApplicationError;
import magma.compile.error.CompileError;
import magma.compile.error.ThrowableError;
import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static magma.compile.Lang.*;

public class Main {

	public static void main(String[] args) {
		if (run() instanceof Some<ApplicationError>(ApplicationError value)) System.err.println(value.display());
	}

	private static Option<ApplicationError> run() {
		final Path javaSourceRoot = Paths.get(".", "src", "main", "java");
		final Path cOutputRoot = Paths.get(".", "src", "main", "windows");

		// Ensure output directory exists
		try {
			Files.createDirectories(cOutputRoot);
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}

		return compileAllJavaFiles(javaSourceRoot, cOutputRoot);
	}

	private static Option<ApplicationError> compileAllJavaFiles(Path javaSourceRoot, Path cOutputRoot) {
		try (Stream<Path> paths = Files.walk(javaSourceRoot)) {
			List<Path> javaFiles =
					paths.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();

			System.out.println("Found " + javaFiles.size() + " Java files to compile");

			for (Path javaFile : javaFiles) {
				System.out.println("Compiling: " + javaFile);
				Option<ApplicationError> result = compileJavaFile(javaFile, javaSourceRoot, cOutputRoot);
				if (result instanceof Some<ApplicationError>(ApplicationError error)) {
					System.err.println("Failed to compile " + javaFile + ": " + error.display());
					return result; // Fail fast - return the error immediately
				}
				System.out.println("Successfully compiled: " + javaFile);
			}

			return Option.empty();
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}
	}

	private static Option<ApplicationError> compileJavaFile(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {
		// Calculate relative path from source root
		Path relativePath = javaSourceRoot.relativize(javaFile);

		// Change extension from .java to .c
		String fileName = relativePath.getFileName().toString();
		String cFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".cpp";
		Path cFilePath = cOutputRoot.resolve(relativePath.getParent()).resolve(cFileName);

		// Ensure output directory exists
		try {
			Files.createDirectories(cFilePath.getParent());
		} catch (IOException e) {
			return Option.of(new ApplicationError(new ThrowableError(e)));
		}

		Result<String, ThrowableError> readResult = readString(javaFile);
		if (readResult instanceof Err<String, ThrowableError>(ThrowableError error))
			return Option.of(new ApplicationError(error));

		if (readResult instanceof Ok<String, ThrowableError>(String input)) {
			Result<String, CompileError> compileResult = compile(input);
			if (compileResult instanceof Err<String, CompileError>(CompileError error))
				return Option.of(new ApplicationError(error));
			if (compileResult instanceof Ok<String, CompileError>(String compiled)) {
				final String message = "// Generated transpiled C++ from '" + Paths.get(".").relativize(javaFile) +
															 "'. This file shouldn't be edited, and rather the compiler implementation should be changed." +
															 System.lineSeparator();
				return writeString(cFilePath, message + compiled).map(ThrowableError::new).map(ApplicationError::new);
			}
		}

		return Option.empty();
	}

	private static Option<IOException> writeString(Path path, String result) {
		try {
			Files.writeString(path, result);
			return Option.empty();
		} catch (IOException e) {
			return Option.of(e);
		}
	}

	private static Result<String, ThrowableError> readString(Path source) {
		try {
			return new Ok<>(Files.readString(source));
		} catch (IOException e) {
			return new Err<>(new ThrowableError(e));
		}
	}

	public static Result<String, CompileError> compile(String input) {
		return JRoot().lex(input)
									.flatMap(node -> Serializers.deserialize(JavaRoot.class, node))
									.flatMap(Main::transform)
									.flatMap(cRoot -> Serializers.serialize(CRoot.class, cRoot))
									.flatMap(CRoot()::generate);
	}

	public static Result<CRoot, CompileError> transform(JavaRoot node) {
		return new Ok<>(new CRoot(node.children()
																	.stream()
																	.map(Main::flattenRootSegment)
																	.flatMap(Collection::stream)
																	.toList()));
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
		for (JStructureSegment child : children) {
			final Tuple<List<CRootSegment>, Option<CDefinition>> tuple = flattenStructureSegment(child, name);
			segments.addAll(tuple.left());
			if (tuple.right() instanceof Some<CDefinition>(CDefinition value)) fields.add(value);
		}

		final Structure structure =
				new Structure(name, fields, new Some<>(System.lineSeparator()), aClass.typeParameters());
		final List<CRootSegment> copy = new ArrayList<>();
		copy.add(structure);
		copy.addAll(segments);
		return copy;
	}

	private static void addRecordParamsAsFields(JStructure aClass, ArrayList<CDefinition> fields) {
		if (aClass instanceof Lang.Record record) {
			Option<List<JDefinition>> params = record.params();
			if (params instanceof Some<List<JDefinition>>(List<JDefinition> paramList)) for (JDefinition param : paramList) {
				final CDefinition cDef = transformDefinition(param);
				fields.add(cDef);
			}
		}
	}

	private static Tuple<List<CRootSegment>, Option<CDefinition>> flattenStructureSegment(JStructureSegment self,
																																												String name) {
		return switch (self) {
			case Invalid invalid -> new Tuple<>(List.of(invalid), new None<>());
			case Method method -> new Tuple<>(List.of(transformMethod(method, name)), new None<>());
			case JStructure jClass -> new Tuple<>(flattenStructure(jClass), new None<>());
			case Field field -> new Tuple<>(Collections.emptyList(), new Some<>(transformDefinition(field.value())));
			case Whitespace _, LineComment _, BlockComment _ -> new Tuple<>(Collections.emptyList(), new None<>());
		};
	}

	private static Function transformMethod(Method method, String structName) {
		final List<JDefinition> oldParams = switch (method.params()) {
			case None<List<JDefinition>> _ -> Collections.emptyList();
			case Some<List<JDefinition>> v -> v.value();
		};

		final List<CParameter> newParams = oldParams.stream().map(Main::transformParameter).toList();

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
				yield segments.stream().map(segment -> {
					return transformFunctionSegment(segment);
				}).toList();
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

	private static CFunctionSegment transformFunctionSegment(JMethodSegment segment) {
		return switch (segment) {
			case JIf anIf -> new CIf(transformExpression(anIf.condition()), transformFunctionSegment(anIf.body()));
			case Invalid invalid -> invalid;
			case Placeholder placeholder -> placeholder;
			case Whitespace whitespace -> whitespace;
			case JReturn aReturn -> new CReturn(transformExpression(aReturn.value()));
			case LineComment lineComment -> lineComment;
			case JBlock jBlock -> new CBlock(jBlock.children().stream().map(Main::transformFunctionSegment).toList());
			case JInitialization jInitialization -> new CInitialization(transformDefinition(jInitialization.definition()),
																																	transformExpression(jInitialization.value()));
			case JAssignment jAssignment ->
					new CAssignment(transformExpression(jAssignment.location()), transformExpression(jAssignment.value()));
			case JPostFix jPostFix -> new CPostFix(transformExpression(jPostFix.value()));
			case JElse jElse -> new CElse(transformFunctionSegment(jElse.child()));
			case Break aBreak -> aBreak;
			case JWhile jWhile ->
					new CWhile(transformExpression(jWhile.condition()), transformFunctionSegment(jWhile.body()));
			case JInvocation invocation -> transformInvocation(invocation);
			case JConstruction jConstruction -> handleConstruction(jConstruction);
			case JDefinition jDefinition -> transformDefinition(jDefinition);
			case Catch aCatch -> new Invalid("???");
			case Switch aSwitch -> new Invalid("???");
			case Try aTry -> new Invalid("???");
		};
	}

	private static CInvocation handleConstruction(JConstruction jConstruction) {
		String name = "new_" + transformType(jConstruction.type()).stringify();
		return new CInvocation(new Identifier(name),
													 jConstruction.arguments()
																				.orElse(new ArrayList<JExpression>())
																				.stream()
																				.map(Main::transformExpression)
																				.toList());
	}

	private static CExpression transformExpression(JExpression expression) {
		return switch (expression) {
			case Invalid invalid -> invalid;
			case Identifier identifier -> identifier;
			case Switch aSwitch -> new Identifier("???");
			case JFieldAccess fieldAccess -> new CFieldAccess(transformExpression(fieldAccess.child()), fieldAccess.name());
			case JInvocation jInvocation -> transformInvocation(jInvocation);
			case JConstruction jConstruction -> handleConstruction(jConstruction);
			case JAdd add -> new CAdd(transformExpression(add.left()), transformExpression(add.right()));
			case JString jString -> new CString(jString.content().orElse(""));
			case JEquals jEquals -> new CEquals(transformExpression(jEquals.left()), transformExpression(jEquals.right()));
			case And and -> new CAnd(transformExpression(and.left()), transformExpression(and.right()));
			case InstanceOf instanceOf -> new Invalid("???");
			case Cast cast -> new Invalid("???");
			case Index index -> new Invalid("???");
			case JLessThan jLessThan -> new Invalid("???");
			case JLessThanEquals jLessThanEquals -> new Invalid("???");
			case JSubtract jSubtract -> new Invalid("???");
			case Not not -> new Invalid("???");
			case Quantity quantity -> new Invalid("???");
			case Lambda lambda -> new Invalid("???");
			case NewArray newArray -> new Invalid("???");
			case CharNode charNode -> charNode;
		};
	}

	private static CInvocation transformInvocation(JInvocation jInvocation) {
		final List<CExpression> newArguments =
				jInvocation.arguments().orElse(new ArrayList<>()).stream().map(Main::transformExpression).toList();
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
			for (JDefinition param : paramList) collectTypeVariables(param.type(), typeVars);

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
				for (JType arg : listOption) collectTypeVariables(arg, typeVars);
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
				yield new CGeneric(generic.base(), listOption.stream().map(Main::transformType).toList());
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
		};
	}
}
