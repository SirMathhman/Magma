struct OrRule(List<Rule> rules) implements Rule{};
Rule Or_OrRule(List<Rule> rules) implements Rule(/*Rule...*/ rules) {}
/*CompileError>*/ lex_OrRule(List<Rule> rules) implements Rule(String content) {}
/*CompileError>*/ generate_OrRule(List<Rule> rules) implements Rule(Node node) {}
