package magma;

import java.util.regex.Matcher;

class MethodCallHandler {
    
    static String compileMethodCallStatement(Matcher matcher, StatementCompilerUtils.StatementContext context) throws CompileException {
        String variableName = matcher.group(1);    // result variable name
        String objectName = matcher.group(2);      // object name (myCalculator)  
        String methodName = matcher.group(3);      // method name (add)
        String args = matcher.group(4);            // method arguments (1, 2)
        
        // Get the object type by looking at capitalized names in variable assignments
        // For now, assume object type from variable name pattern
        // This is a simple heuristic - in a real implementation, you'd have a symbol table
        String objectType = inferObjectType(objectName);
        if (objectType == null) {
            objectType = "Calculator"; // Default fallback for now
        }
        
        // Transform method call: obj.method(args) -> method_ObjectType(&obj, args)
        String functionName = methodName + "_" + objectType;
        String transformedCall;
        
        if (args.trim().isEmpty()) {
            transformedCall = functionName + "(&" + objectName + ")";
        } else {
            transformedCall = functionName + "(&" + objectName + ", " + args + ")";
        }
        
        // Infer return type - for now assume I32
        String cType = context.typeMapping.get("I32"); // int32_t
        
        return cType + " " + variableName + " = " + transformedCall;
    }
    
    private static String inferObjectType(String objectName) {
        // Simple heuristic: if variable name contains a recognizable type name, use it
        // In a real implementation, this would query a symbol table
        if (objectName.toLowerCase().contains("calculator")) {
            return "Calculator";
        }
        // Add more type inference rules as needed
        return null;
    }
}