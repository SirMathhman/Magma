struct InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule{};
Rule First_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Rule left, char* infix, Rule right) {}
Rule Last_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Rule leftRule, char* infix, Rule rightRule) {}
/*CompileError>*/ lex_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(char* input) {}
/*CompileError>*/ generate_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule(Node node) {}
