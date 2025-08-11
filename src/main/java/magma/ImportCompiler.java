package magma;

class ImportCompiler {
	static String compileImportStatement(String moduleName) {
		// Handle different module imports
		switch (moduleName) {
			case "stdio":
				return "#include <stdio.h>";
			default:
				// For unknown modules, return a generic include
				return "#include <" + moduleName + ".h>";
		}
	}
}