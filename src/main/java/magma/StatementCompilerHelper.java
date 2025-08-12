package magma;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StatementCompilerHelper {
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^import\\s+(\\w+);?$");
    private static final Pattern EXTERN_PATTERN = Pattern.compile("^extern\\s+fn\\s+([^;]+);?$");

    static String tryCompileImportStatement(String stmt) throws CompileException {
        if (stmt.startsWith("import")) {
            // Validate import syntax
            if (stmt.trim().equals("import") || stmt.trim().equals("import;")) {
                throw new CompileException("Import statement missing module name");
            }
            if (!stmt.contains(";") && !stmt.endsWith(";")) {
                throw new CompileException("Import statement missing semicolon");
            }
        }
        
        Matcher importMatcher = IMPORT_PATTERN.matcher(stmt);
        if (importMatcher.matches()) {
            String moduleName = importMatcher.group(1);
            return ImportCompiler.compileImportStatement(moduleName);
        }
        
        return null;
    }

    static String tryCompileExternStatement(String stmt) throws CompileException {
        if (stmt.startsWith("extern")) {
            // Validate extern syntax
            if (stmt.trim().equals("extern") || stmt.trim().equals("extern;")) {
                throw new CompileException("Extern statement missing function declaration");
            }
            if (!stmt.contains("fn")) {
                throw new CompileException("Extern statement missing 'fn' keyword");
            }
            if (!stmt.contains(":")) {
                throw new CompileException("Extern function missing return type");
            }
        }
        
        Matcher externMatcher = EXTERN_PATTERN.matcher(stmt);
        if (externMatcher.matches()) {
            // Extern statements are for type inference only, don't generate code
            return "";
        }
        
        return null;
    }
}