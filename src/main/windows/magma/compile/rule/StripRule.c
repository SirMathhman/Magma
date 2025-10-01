struct StripRule(String leftKey, Rule rule, String rightKey) implements Rule{};
Rule Strip_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(Rule rule) {}
Rule Strip_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(String left,  Rule rule,  String right) {}
CompileError> lex_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(String content) {}
CompileError> generate_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(Node node) {}
