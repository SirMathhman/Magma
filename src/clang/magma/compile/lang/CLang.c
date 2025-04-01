#include "CLang.h"
magma.compile.rule.Rule createCRootRule(){return TypeRule("root", CommonLang.createBlockRule(createCRootSegmentRule()));
}
magma.compile.rule.Rule createCRootSegmentRule(){return OrRule(Lists.of(createIncludeRule(), TypeRule("ifndef", PrefixRule("#ifndef ", SuffixRule(StringRule("value"), "\n"))), TypeRule("define", PrefixRule("#define ", SuffixRule(StringRule("value"), "\n"))), TypeRule("endif", PrefixRule("#endif\n", EmptyRule())), createStructRule(), createFunctionRule(), createExpansionRule()));
}
magma.compile.rule.tree.TypeRule createExpansionRule(){Rule type = createTypeRule();
        Rule typeArguments = new NodeListRule("arguments", new FoldingDivider(new ValueFolder()), new OrRule(Lists.of(
                createWhitespaceRule(),
                type
        )));

        Rule base = new NodeRule("base", createQualifiedRule());return TypeRule("expansion", PrefixRule("// expand ", SuffixRule(StripRule(SuffixRule(InfixRule(base, "<", typeArguments, FirstLocator()), ">")), "\n")));
}
magma.compile.rule.Rule createFunctionRule(){Rule definitionRule = createDefinitionsRule();
        Rule definition = new NodeRule("definition", definitionRule);
        Rule params = CommonLang.createParamsRule(definitionRule);

        Rule block = CommonLang.createContentRule(new StripRule(new SuffixRule(params, ")")), createStatementRule());
        Rule block1 = new OptionalNodeRule("content",
                block,
                new SuffixRule(params, ");\n")
        );return TypeRule("function", InfixRule(definition, "(", block1, FirstLocator()));
}
magma.compile.rule.Rule createStatementRule(){return OrRule(Lists.of(createWhitespaceRule(), createReturnRule(createValueRule()), createIfRule(), SuffixRule(createInvocationRule(createValueRule()), ";"), createForRule(), createAssignmentRule(), createPostfixRule(), createElseRule(), createWhileRule()));
}
magma.compile.rule.Rule createValueRule(){LazyRule value = new LazyRule();value.set(OrRule(Lists.of(createSymbolValueRule(), createOperatorRule(value, "add", "+"), createOperatorRule(value, "subtract", "-"), createOperatorRule(value, "and", "&&"), createOperatorRule(value, "or", "||"), createInvocationRule(value), createAccessRule("data-access", ".", value), createStringRule(), createTernaryRule(value), createNumberRule(), createNotRule(value), createCharRule())));return value;
}
magma.compile.rule.Rule createStructRule(){Rule name = CommonLang.createNamedWithTypeParams();
        Rule contentRule = CommonLang.createContentRule(name, createStructMemberRule());return TypeRule("struct", PrefixRule("struct ", SuffixRule(contentRule, ";\n")));
}
magma.compile.rule.Rule createStructMemberRule(){return SuffixRule(createDefinitionsRule(), ";");
}
magma.compile.rule.tree.OrRule createDefinitionsRule(){return OrRule(Lists.of(TypeRule("definition", CommonLang.createDefinitionRule(createTypeRule())), createFunctionalDefinitionType()));
}
magma.compile.rule.Rule createFunctionalDefinitionType(){Rule returns = new NodeRule("return", createTypeRule());
        Rule params = new NodeListRule("params", new FoldingDivider(new ValueFolder()), createTypeRule());
        Rule maybeParams = new OptionalNodeListRule("params", params, new EmptyRule());
        Rule right = new InfixRule(new PrefixRule("*", new StringRule("name")), ")(", new SuffixRule(maybeParams, ")"), new FirstLocator());return TypeRule("functional-definition", InfixRule(returns, "(", right, FirstLocator()));
}
magma.compile.rule.Rule createTypeRule(){LazyRule type = new LazyRule();type.set(OrRule(Lists.of(TypeRule("struct-type", PrefixRule("struct ", StringRule("value"))), TypeRule("ref", SuffixRule(NodeRule("child", type), "*")), createQualifiedRule(), CommonLang.createGenericRule(type))));return type;
}
magma.compile.rule.Rule createIncludeRule(){NodeListRule path = new NodeListRule("path", new CharDivider('/'), new StringRule("value"));return TypeRule("include", PrefixRule("#include \"", SuffixRule(path, ".h\"\n")));
}
