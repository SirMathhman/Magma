package magma;

class FunctionDefinitionExtractor {
    
    static InnerFunctionProcessor.InnerFunctionMatch extractFunctionDefinition(String text, int start) {
        FunctionParseContext context = new FunctionParseContext(text, start + 3);
        
        context.pos = skipWhitespace(text, context.pos);
        
        String name = extractFunctionName(text, context.pos);
        if (name == null) return null;
        context.pos += name.length();
        
        context.pos = skipWhitespace(text, context.pos);
        
        if (!expectOpenParen(text, context.pos)) return null;
        context.pos++;
        
        ParameterResult paramResult = extractParametersWithPos(text, context.pos);
        if (paramResult == null) return null;
        String params = paramResult.params;
        context.pos = paramResult.endPos;
        
        context.pos = skipWhitespace(text, context.pos);
        
        ReturnTypeResult returnTypeResult = extractReturnTypeWithPos(text, context.pos);
        String returnType = null;
        if (returnTypeResult != null) {
            returnType = returnTypeResult.returnType;
            context.pos = returnTypeResult.endPos;
            context.pos = skipWhitespace(text, context.pos);
        }
        
        FunctionComponents components = new FunctionComponents(name, returnType);
        components.setParams(params);
        BuildContext buildContext = new BuildContext(context, start);
        buildContext.setComponents(components);
        return buildFunctionMatch(text, buildContext);
    }
    
    private static InnerFunctionProcessor.InnerFunctionMatch buildFunctionMatch(String text, BuildContext context) {
        if (!expectArrow(text, context.parseContext.pos)) return null;
        context.parseContext.pos += 2;
        
        context.parseContext.pos = skipWhitespace(text, context.parseContext.pos);
        
        if (!expectOpenBrace(text, context.parseContext.pos)) return null;
        context.parseContext.pos++;
        
        BodyResult bodyResult = extractBodyWithPos(text, context.parseContext.pos);
        if (bodyResult == null) return null;
        String body = bodyResult.body;
        context.parseContext.pos = bodyResult.endPos;
        
        String fullMatch = text.substring(context.start, context.parseContext.pos);
        InnerFunctionProcessor.FunctionComponents processorComponents = new InnerFunctionProcessor.FunctionComponents(context.components.name, context.components.returnType);
        processorComponents.setParams(context.components.params);
        InnerFunctionProcessor.MatchData data = new InnerFunctionProcessor.MatchData(processorComponents, body);
        data.setFullMatch(fullMatch);
        return new InnerFunctionProcessor.InnerFunctionMatch(data);
    }
    
    private static boolean expectOpenParen(String text, int pos) {
        return pos < text.length() && text.charAt(pos) == '(';
    }
    
    private static boolean expectOpenBrace(String text, int pos) {
        return pos < text.length() && text.charAt(pos) == '{';
    }
    
    private static boolean expectArrow(String text, int pos) {
        return pos + 1 < text.length() && text.charAt(pos) == '=' && text.charAt(pos + 1) == '>';
    }
    
    static class FunctionParseContext {
        final String text;
        int pos;
        
        FunctionParseContext(String text, int pos) {
            this.text = text;
            this.pos = pos;
        }
    }
    
    static class FunctionComponents {
        final String name;
        String params;
        final String returnType;
        
        FunctionComponents(String name, String returnType) {
            this.name = name;
            this.returnType = returnType;
        }
        
        void setParams(String params) {
            this.params = params;
        }
    }
    
    static class BuildContext {
        final FunctionParseContext parseContext;
        final int start;
        FunctionComponents components;
        
        BuildContext(FunctionParseContext parseContext, int start) {
            this.parseContext = parseContext;
            this.start = start;
        }
        
        void setComponents(FunctionComponents components) {
            this.components = components;
        }
    }
    
    private static int skipWhitespace(String text, int pos) {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) pos++;
        return pos;
    }
    
    private static String extractFunctionName(String text, int pos) {
        int nameStart = pos;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_')) pos++;
        if (pos == nameStart) return null;
        return text.substring(nameStart, pos);
    }
    
    
    private static ParameterResult extractParametersWithPos(String text, int pos) {
        int paramsStart = pos;
        int parenDepth = 1;
        while (pos < text.length() && parenDepth > 0) {
            if (text.charAt(pos) == '(') parenDepth++;
            else if (text.charAt(pos) == ')') parenDepth--;
            pos++;
        }
        if (parenDepth != 0) return null;
        String params = text.substring(paramsStart, pos - 1);
        return new ParameterResult(params, pos);
    }
    
    static class ParameterResult {
        final String params;
        final int endPos;
        
        ParameterResult(String params, int endPos) {
            this.params = params;
            this.endPos = endPos;
        }
    }
    
    
    private static ReturnTypeResult extractReturnTypeWithPos(String text, int pos) {
        if (pos >= text.length() || text.charAt(pos) != ':') return null;
        pos++; // Skip ':'
        pos = skipWhitespace(text, pos);
        int returnTypeStart = pos;
        while (pos < text.length() && (Character.isLetterOrDigit(text.charAt(pos)) || text.charAt(pos) == '_')) pos++;
        if (pos > returnTypeStart) {
            String returnType = text.substring(returnTypeStart, pos);
            return new ReturnTypeResult(returnType, pos);
        }
        return null;
    }
    
    
    private static BodyResult extractBodyWithPos(String text, int pos) {
        int bodyStart = pos;
        int braceDepth = 1;
        while (pos < text.length() && braceDepth > 0) {
            if (text.charAt(pos) == '{') braceDepth++;
            else if (text.charAt(pos) == '}') braceDepth--;
            pos++;
        }
        if (braceDepth != 0) return null;
        String body = text.substring(bodyStart, pos - 1);
        return new BodyResult(body, pos);
    }
    
    static class BodyResult {
        final String body;
        final int endPos;
        
        BodyResult(String body, int endPos) {
            this.body = body;
            this.endPos = endPos;
        }
    }
    
    static class ReturnTypeResult {
        final String returnType;
        final int endPos;
        
        ReturnTypeResult(String returnType, int endPos) {
            this.returnType = returnType;
            this.endPos = endPos;
        }
    }
}