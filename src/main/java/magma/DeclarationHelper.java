package magma;

import java.util.Map;

/**
 * Helper class that handles declaration-related functionality for Magma compiler.
 */
public class DeclarationHelper {
    
    /**
     * Handles typed declaration parsing
     */
    public static Declaration handleTypedDeclaration(String s, Map<String, VarInfo> env, String stmt)
            throws CompileException {
        int eqIdx = s.indexOf("=");
        if (eqIdx < 0) throw new CompileException("Invalid input", stmt);
        
        // Extract and validate type
        String typeStr = s.substring(0, eqIdx).trim();
        String cType = TypeHelper.mapType(typeStr);
        if (cType == null) throw new CompileException("Invalid type: " + typeStr, stmt);
        String valuePart = s.substring(eqIdx + 1).trim();

        // Handle based on type
        if ("bool".equals(cType)) {
            return handleBooleanTypedDeclaration(valuePart, env, stmt, cType);
        }
        
        if (cType.contains("int") || cType.contains("uint")) {
            return handleNumericTypedDeclaration(valuePart, env, stmt, cType);
        }
        
        // If we get here and cType is not null, it must be a struct type
        // since all primitive types are handled above
        return handleStructTypedDeclaration(valuePart, env, stmt, cType);
    }
    
    /**
     * Handles struct typed declarations
     */
    private static Declaration handleStructTypedDeclaration(String valuePart, Map<String, VarInfo> env, String stmt, String cType)
            throws CompileException {
        // Check if it's a struct initialization
        if (StructHelper.isStructInitialization(valuePart)) {
            return StructHelper.processStructInitialization(valuePart, env, stmt);
        }
        
        // Check for identifier with matching type
        if (TypeHelper.isIdentifier(valuePart)) {
            VarInfo var = env.get(valuePart);
            if (var != null && var.cType().equals(cType)) {
                return new Declaration(cType, valuePart);
            }
        }
        
        throw new CompileException("Invalid struct initialization", stmt);
    }
    
    /**
     * Handles boolean typed declarations
     */
    private static Declaration handleBooleanTypedDeclaration(String valuePart, Map<String, VarInfo> env, String stmt, String cType)
            throws CompileException {
        // Try comparison expression first
        Declaration cmp = ValueResolver.tryParseComparison(valuePart, env, stmt);
        if (cmp != null) {
            return cmp; // already has bool type
        }
        
        // Check for boolean literals
        if ("true".equals(valuePart) || "false".equals(valuePart)) {
            return new Declaration(cType, valuePart);
        }
        
        throw new CompileException("Invalid boolean value", stmt);
    }
    
    /**
     * Handles numeric typed declarations
     */
    private static Declaration handleNumericTypedDeclaration(String valuePart, Map<String, VarInfo> env, String stmt, String cType)
            throws CompileException {
        // Check for numeric literals
        if (valuePart.matches("\\d+")) {
            return new Declaration(cType, valuePart);
        }
        
        // Check for identifier with matching type
        if (TypeHelper.isIdentifier(valuePart)) {
            VarInfo var = env.get(valuePart);
            if (var != null && var.cType().equals(cType)) {
                return new Declaration(cType, valuePart);
            }
        }
        
        throw new CompileException("Invalid numeric value", stmt);
    }
    
    /**
     * Parses a declaration starting with ':' or '='
     */
    public static Declaration parseDeclaration(String rest, Map<String, VarInfo> env, String stmt)
            throws CompileException {
        if (rest.startsWith(":")) {
            return handleTypedDeclaration(rest.substring(1).trim(), env, stmt);
        }
        if (rest.startsWith("=")) {
            String expr = rest.substring(1).trim();
            Declaration cmp = ValueResolver.tryParseComparison(expr, env, stmt);
            if (cmp != null) return cmp;
            return ValueResolver.resolveSimpleValue(expr, env, stmt);
        }
        throw new CompileException("Invalid input", stmt);
    }
}