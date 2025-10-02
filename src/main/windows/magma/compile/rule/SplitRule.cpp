// Generated transpiled C++ from 'src\main\java\magma\compile\rule\SplitRule.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct SplitRule{Rule leftRule;, Rule rightRule;, Splitter splitter;};
Rule First_SplitRule(Rule left, char* infix, Rule right) {}
Rule Last_SplitRule(Rule leftRule, char* infix, Rule rightRule) {}
Rule Split_SplitRule(Rule left, Splitter splitter, Rule right) {}
Result<Node, CompileError> lex_SplitRule(char* input) {}
Result<String, CompileError> generate_SplitRule(Node node) {}
