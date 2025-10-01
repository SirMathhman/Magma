struct PlaceholderRule(Rule rule) implements Rule{};
String wrap_PlaceholderRule(Rule rule) implements Rule(String input) {}
Rule Placeholder_PlaceholderRule(Rule rule) implements Rule(Rule rule) {}
CompileError> lex_PlaceholderRule(Rule rule) implements Rule(String content) {}
CompileError> generate_PlaceholderRule(Rule rule) implements Rule(Node node) {}
