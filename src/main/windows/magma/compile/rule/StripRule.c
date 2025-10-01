struct StripRule(String leftKey, Rule rule, String rightKey) implements Rule{};
Rule Strip_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(Rule rule) {}
Rule Strip_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(char* left, Rule rule, char* right) {}
/*CompileError>*/ lex_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(char* content) {}
/*CompileError>*/ generate_StripRule(String leftKey, Rule rule, String rightKey) implements Rule(Node node) {}
