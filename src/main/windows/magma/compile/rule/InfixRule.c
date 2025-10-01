struct InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule {};
Rule First_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule 
Rule Last_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule 
CompileError> lex_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule 
CompileError> generate_InfixRule(Rule leftRule, String infix, Rule rightRule, Locator locator) implements Rule 
