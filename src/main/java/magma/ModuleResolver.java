package magma;

import magma.ast.*;
import magma.lexer.Lexer;
import magma.lexer.Token;
import magma.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ModuleResolver {
	private final Path baseDirectory;
	private final Map<String, Program> resolvedModules = new HashMap<>();
	private final Set<String> resolving = new HashSet<>();

	public ModuleResolver(Path baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public Program resolveModule(String modulePath) throws IOException {
		Path filePath = baseDirectory.resolve(modulePath + ".mgs");
		if (!Files.exists(filePath)) {
			// Try relative to the base directory
			filePath = baseDirectory.resolve(modulePath.replace('.', '/') + ".mgs");
		}
		if (!Files.exists(filePath)) {
			throw new IOException("Module not found: " + modulePath);
		}

		String canonicalPath = filePath.normalize().toString();
		if (resolvedModules.containsKey(canonicalPath)) {
			return resolvedModules.get(canonicalPath);
		}

		if (resolving.contains(canonicalPath)) {
			throw new IOException("Circular import detected: " + modulePath);
		}

		resolving.add(canonicalPath);

		try {
			String source = Files.readString(filePath);
			Lexer lexer = new Lexer(source);
			List<Token> tokens = lexer.tokenize();
			Parser parser = new Parser(tokens);
			Program program = parser.parse();

			// Resolve imports recursively
			Path moduleDir = filePath.getParent();
			ModuleResolver resolver = new ModuleResolver(moduleDir);
			for (ImportStatement imp : program.getImports()) {
				if (!imp.isExtern()) {
					String importedModule = imp.getModule();
					Program importedProgram = resolver.resolveModule(importedModule);
					// Merge imported program's definitions
					mergeProgram(program, importedProgram);
				}
			}

			resolvedModules.put(canonicalPath, program);
			return program;
		} finally {
			resolving.remove(canonicalPath);
		}
	}

	private void mergeProgram(Program target, Program source) {
		// Merge type definitions (avoid duplicates)
		Set<String> existingTypeNames = new HashSet<>();
		for (TypeDefinition td : target.getTypeDefinitions()) {
			existingTypeNames.add(td.getName());
		}
		for (TypeDefinition td : source.getTypeDefinitions()) {
			if (!existingTypeNames.contains(td.getName())) {
				target.getTypeDefinitions().add(td);
				existingTypeNames.add(td.getName());
			}
		}

		// Merge traits (avoid duplicates)
		Set<String> existingTraitNames = new HashSet<>();
		for (TraitDefinition td : target.getTraits()) {
			existingTraitNames.add(td.getName());
		}
		for (TraitDefinition td : source.getTraits()) {
			if (!existingTraitNames.contains(td.getName())) {
				target.getTraits().add(td);
				existingTraitNames.add(td.getName());
			}
		}

		// Merge trait implementations
		target.getTraitImplementations().addAll(source.getTraitImplementations());

		// Merge functions
		target.getFunctions().addAll(source.getFunctions());

		// Merge extern functions
		target.getExternFunctions().addAll(source.getExternFunctions());
	}

	public static Program resolveMainModule(String mainFilePath) throws IOException {
		Path mainPath = Paths.get(mainFilePath);
		Path baseDir = mainPath.getParent();
		if (baseDir == null) {
			baseDir = Paths.get(".");
		}

		ModuleResolver resolver = new ModuleResolver(baseDir);
		String moduleName = mainPath.getFileName().toString();
		if (moduleName.endsWith(".mgs")) {
			moduleName = moduleName.substring(0, moduleName.length() - 4);
		}
		return resolver.resolveModule(moduleName);
	}
}

