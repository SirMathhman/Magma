struct InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule{};
Rule First_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Rule left String infix Rule right) {}
Rule Last_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Rule leftRule String infix Rule rightRule) {}
CompileError> lex_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(String input) {}
CompileError> generate_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Node node) {}
