package com.example.magma;

public class ErrorService {

    public String translateSyntax(String input) {
        if (input.isEmpty()) {
            return "";
        } else if (input.startsWith("let ") && input.endsWith(";")) {
            return translateVariableDeclaration(input);
        } else if (input.startsWith("fn ") && input.contains(" : ") && input.endsWith(" => {}")) {
            return translateFunctionDeclaration(input);
        } else {
            throw new RuntimeException("Error processing unsupported input");
        }
    }
    
    private String translateVariableDeclaration(String input) {
        // Parse "let varName : Type = value;"
        String content = input.substring(4, input.length() - 1); // Remove "let " and ";"
        String[] parts = content.split(" : ");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid variable declaration format");
        }
        
        String varName = parts[0].trim();
        String[] typeAndValue = parts[1].split(" = ");
        if (typeAndValue.length != 2) {
            throw new RuntimeException("Invalid variable declaration format");
        }
        
        String type = typeAndValue[0].trim();
        String value = typeAndValue[1].trim();
        
        String cType = mapTypeToCType(type);
        return "#include <stdint.h>\n" + cType + " " + varName + " = " + value + ";";
    }
    
    private String translateFunctionDeclaration(String input) {
        // Parse "fn funcName() : ReturnType => {}"
        String content = input.substring(3, input.length() - 6); // Remove "fn " and " => {}"
        String[] parts = content.split(" : ");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid function declaration format");
        }
        
        String functionSignature = parts[0].trim();
        String returnType = parts[1].trim();
        
        String javaReturnType = mapTypeToJavaType(returnType);
        return javaReturnType + " " + functionSignature + "{}";
    }
    
    private String mapTypeToCType(String type) {
        switch (type) {
            case "I8": return "int8_t";
            case "I16": return "int16_t";
            case "I32": return "int32_t";
            case "I64": return "int64_t";
            case "U8": return "uint8_t";
            case "U16": return "uint16_t";
            case "U32": return "uint32_t";
            case "U64": return "uint64_t";
            default:
                throw new RuntimeException("Unsupported type: " + type);
        }
    }
    
    private String mapTypeToJavaType(String type) {
        switch (type) {
            case "Void": return "void";
            case "I8": return "int8_t";
            case "I16": return "int16_t";
            case "I32": return "int32_t";
            case "I64": return "int64_t";
            case "U8": return "uint8_t";
            case "U16": return "uint16_t";
            case "U32": return "uint32_t";
            case "U64": return "uint64_t";
            default:
                throw new RuntimeException("Unsupported type: " + type);
        }
    }
}