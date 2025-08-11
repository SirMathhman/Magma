package magma;

class MultipleConstructsParser {
	private static class ParseContext {
		StringBuilder current;
		StringBuilder result;

		ParseContext(StringBuilder current, StringBuilder result) {
			this.current = current;
			this.result = result;
		}
	}

	private static class ParserState {
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

	private static class DeclarationParams {
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
			String token = tokens[i];

			if (token.equals("struct"))
				i = handleStructDeclaration(new ParserState(new ParserInput(new ParseData(tokens, i), context)));
			else if (token.equals("fn"))
				i = handleFunctionDeclaration(new ParserState(new ParserInput(new ParseData(tokens, i), context)));
			else {
				current.append(token).append(" ");
				i++;
			}
		}

		if (current.length() > 0) result.append(compileConstruct(current.toString().trim()));

		return result.toString().trim();
	}

	private int handleStructDeclaration(ParserState state) throws CompileException {
		return handleDeclaration(new DeclarationParams(state, "struct "));
	}

	private int handleFunctionDeclaration(ParserState state) throws CompileException {
		return handleDeclaration(new DeclarationParams(state, "fn "));
	}

	private int handleDeclaration(DeclarationParams params) throws CompileException {
		if (params.state.context.current.length() > 0) {
			params.state.context.result.append(compileConstruct(params.state.context.current.toString().trim())).append(" ");
			params.state.context.current.setLength(0);
		}

		params.state.context.current.append(params.prefix);
		int i = params.state.i + 1;
		while (i < params.state.tokens.length) {
			String token = params.state.tokens[i];
			params.state.context.current.append(token).append(" ");
			// Handle both standalone } and tokens ending with }
			if (token.endsWith("}")) break;
			i++;
		}
		params.state.context.result.append(compileConstruct(params.state.context.current.toString().trim())).append(" ");
		params.state.context.current.setLength(0);
		return i + 1;
	}

	private String compileConstruct(String construct) throws CompileException {
		return Compiler.compileConstruct(construct);
	}
}