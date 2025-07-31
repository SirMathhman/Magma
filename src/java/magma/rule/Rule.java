package magma.rule;

import magma.error.CompileError;
import magma.node.Node;
import magma.result.Result;

/**
 * Represents a rule for lexing (parsing) input text into nodes and generating output text from nodes.
 * Rules are the core components of the language processing system, handling both the parsing
 * of input text and the generation of output text.
 */
public interface Rule {
	/**
	 * Generates output text from a node.
	 *
	 * @param node the node to generate text from
	 * @return a Result containing either the generated text (Ok) or a CompileError (Err)
	 */
	Result<String, CompileError> generate(Node node);

	/**
	 * Lexes (parses) input text into a node.
	 *
	 * @param input the input text to lex
	 * @return a Result containing either the lexed node (Ok) or a CompileError (Err)
	 */
	Result<Node, CompileError> lex(String input);
}
