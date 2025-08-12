package magma;

class MultipleConstructsParser {
	static class ParseContext {
		StringBuilder current;
		StringBuilder result;

		ParseContext(StringBuilder current, StringBuilder result) {
			this.current = current;
			this.result = result;
		}
	}

	static class ParserState {
		final String[] tokens;
		final int i;
		final ParseContext context;

		ParserState(ParserInput input) {
			this.tokens = input.tokens;
			this.i = input.i;
			this.context = input.context;
		}
	}

	private static class ParserInput {
		final String[] tokens;
		final int i;
		final ParseContext context;

		ParserInput(ParseData data, ParseContext context) {
			this.tokens = data.tokens;
			this.i = data.i;
			this.context = context;
		}
	}

	private static class ParseData {
		final String[] tokens;
		final int i;

		ParseData(String[] tokens, int i) {
			this.tokens = tokens;
			this.i = i;
		}
	}

	static class DeclarationParams {
		final ParserState state;
		final String prefix;

		DeclarationParams(ParserState state, String prefix) {
			this.state = state;
			this.prefix = prefix;
		}
	}

	public String parse(String input) throws CompileException {
		StringBuilder result = new StringBuilder();
		StringBuilder current = new StringBuilder();
		ParseContext context = new ParseContext(current, result);

		String[] tokens = input.split("\\s+");
		int i = 0;

		while (i < tokens.length) {
			i = processToken(new TokenProcessorInput(new ParseData(tokens, i), context));
		}

		if (current.length() > 0) result.append(compileConstruct(current.toString().trim()));

		return result.toString().trim();
	}

	private int processToken(TokenProcessorInput input) throws CompileException {
		String token = input.parseData.tokens[input.parseData.i];

		if (token.equals("struct"))
			return handleStructDeclaration(new ParserState(new ParserInput(input.parseData, input.context)));
		else if (token.equals("class") && input.parseData.i + 1 < input.parseData.tokens.length && input.parseData.tokens[input.parseData.i + 1].equals("fn"))
			return handleClassDeclaration(new ParserState(new ParserInput(input.parseData, input.context)));
		else if (token.equals("extern") && input.parseData.i + 1 < input.parseData.tokens.length && input.parseData.tokens[input.parseData.i + 1].equals("fn"))
			return handleExternDeclaration(new ParserState(new ParserInput(input.parseData, input.context)));
		else if (token.equals("fn"))
			return handleFunctionDeclaration(new ParserState(new ParserInput(input.parseData, input.context)));
		else {
			input.context.current.append(token).append(" ");
			return input.parseData.i + 1;
		}
	}

	static class TokenProcessorInput {
		final ParseData parseData;
		final ParseContext context;

		TokenProcessorInput(ParseData parseData, ParseContext context) {
			this.parseData = parseData;
			this.context = context;
		}
	}

	private int handleExternDeclaration(ParserState state) throws CompileException {
		// Handle extern function declarations - they don't generate code, just for type inference
		if (state.context.current.length() > 0) {
			state.context.result.append(compileConstruct(state.context.current.toString().trim())).append(" ");
			state.context.current.setLength(0);
		}
		
		return skipToSemicolon(state.tokens, state.i);
	}

	private int skipToSemicolon(String[] tokens, int startIndex) {
		int i = startIndex;
		BracketCounter counter = new BracketCounter();
		
		while (i < tokens.length) {
			String token = tokens[i];
			counter.countBrackets(token);
			
			// If we find a semicolon and we're not inside any brackets, we're done
			if (token.endsWith(";") && counter.allBracketsBalanced()) {
				i++; // Skip the token with semicolon
				break;
			}
			
			i++;
		}
		
		return i;
	}

	static class BracketCounter {
		private int angleDepth = 0;
		private int squareDepth = 0;
		private int parenDepth = 0;

		void countBrackets(String token) {
			for (char c : token.toCharArray()) {
				if (c == '<') angleDepth++;
				else if (c == '>') angleDepth--;
				else if (c == '[') squareDepth++;
				else if (c == ']') squareDepth--;
				else if (c == '(') parenDepth++;
				else if (c == ')') parenDepth--;
			}
		}

		boolean allBracketsBalanced() {
			return angleDepth == 0 && squareDepth == 0 && parenDepth == 0;
		}
	}

	private int handleStructDeclaration(ParserState state) throws CompileException {
		return handleDeclaration(new DeclarationParams(state, "struct "));
	}

	private int handleFunctionDeclaration(ParserState state) throws CompileException {
		return handleDeclaration(new DeclarationParams(state, "fn "));
	}

	private int handleClassDeclaration(ParserState state) throws CompileException {
		return ClassDeclarationHandler.handleClassDeclaration(state);
	}

	private int handleDeclaration(DeclarationParams params) throws CompileException {
		if (params.state.context.current.length() > 0) {
			params.state.context.result.append(compileConstruct(params.state.context.current.toString().trim())).append(" ");
			params.state.context.current.setLength(0);
		}

		params.state.context.current.append(params.prefix);
		int i = params.state.i + 1;
		int braceDepth = 0;
		boolean foundOpeningBrace = false;
		
		while (i < params.state.tokens.length) {
			String token = params.state.tokens[i];
			params.state.context.current.append(token).append(" ");
			
			// Count braces
			for (char c : token.toCharArray()) {
				if (c == '{') {
					braceDepth++;
					foundOpeningBrace = true;
				} else if (c == '}') {
					braceDepth--;
				}
			}
			
			// Handle both standalone } and tokens ending with }
			if (token.endsWith("}") && braceDepth == 0) break;
			i++;
		}
		
		// Validate that braces are properly balanced
		if (foundOpeningBrace && braceDepth != 0) {
			throw new CompileException("Mismatched braces in declaration");
		}
		
		params.state.context.result.append(compileConstruct(params.state.context.current.toString().trim())).append(" ");
		params.state.context.current.setLength(0);
		return i + 1;
	}

	static String compileConstruct(String construct) throws CompileException {
		return Compiler.compileConstruct(construct);
	}
}