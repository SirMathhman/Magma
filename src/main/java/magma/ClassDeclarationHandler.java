package magma;

class ClassDeclarationHandler {
    
    static int handleClassDeclaration(MultipleConstructsParser.ParserState state) throws CompileException {
        MultipleConstructsParser.DeclarationParams params = new MultipleConstructsParser.DeclarationParams(state, "class fn ");
        if (params.state.context.current.length() > 0) {
            params.state.context.result.append(MultipleConstructsParser.compileConstruct(params.state.context.current.toString().trim())).append(" ");
            params.state.context.current.setLength(0);
        }

        params.state.context.current.append(params.prefix);
        int i = params.state.i + 2; // Skip both "class" and "fn"
        
        ClassProcessingData data = new ClassProcessingData(params, i);
        i = processClassTokens(data);
        
        validateClassBraces(data.tracker);
        
        params.state.context.result.append(MultipleConstructsParser.compileConstruct(params.state.context.current.toString().trim())).append(" ");
        params.state.context.current.setLength(0);
        return i + 1;
    }

    private static int processClassTokens(ClassProcessingData data) {
        int i = data.startIndex;
        while (i < data.params.state.tokens.length) {
            String token = data.params.state.tokens[i];
            data.params.state.context.current.append(token).append(" ");
            
            data.tracker.countBraces(token);
            
            // Stop when we've balanced all braces after finding at least one opening brace
            if (data.tracker.isComplete()) {
                break;
            }
            
            i++;
        }
        return i;
    }

    private static void validateClassBraces(ClassBraceTracker tracker) throws CompileException {
        if (tracker.hasOpeningBrace() && !tracker.isBalanced()) {
            throw new CompileException("Mismatched braces in class declaration");
        }
        if (!tracker.hasOpeningBrace()) {
            throw new CompileException("Missing opening brace in class declaration");
        }
    }

    static class ClassProcessingData {
        final MultipleConstructsParser.DeclarationParams params;
        final int startIndex;
        final ClassBraceTracker tracker;

        ClassProcessingData(MultipleConstructsParser.DeclarationParams params, int startIndex) {
            this.params = params;
            this.startIndex = startIndex;
            this.tracker = new ClassBraceTracker();
        }
    }

    static class ClassBraceTracker {
        private int braceDepth = 0;
        private boolean foundOpeningBrace = false;

        void countBraces(String token) {
            for (char c : token.toCharArray()) {
                if (c == '{') {
                    braceDepth++;
                    foundOpeningBrace = true;
                } else if (c == '}') {
                    braceDepth--;
                }
            }
        }

        boolean isComplete() {
            return foundOpeningBrace && braceDepth == 0;
        }

        boolean hasOpeningBrace() {
            return foundOpeningBrace;
        }

        boolean isBalanced() {
            return braceDepth == 0;
        }
    }
}