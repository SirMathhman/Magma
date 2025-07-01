package magma.lang;

import magma.compile.result.ResultFactoryImpl;
import magma.error.FormatError;
import magma.node.EverythingNode;
import magma.node.result.NodeResult;
import magma.rule.Rule;
import magma.rule.SplitRule;
import magma.rule.locate.FirstLocator;
import magma.rule.locate.LastLocator;
import magma.rule.split.InfixSplitter;
import magma.string.result.StringResult;

class CommonLang {
    private CommonLang() {}

    public static Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> Last(final Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> leftRule,
                                                                                                                final String infix,
                                                                                                                final Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> rightRule) {
        return new SplitRule<>(leftRule, rightRule, new InfixSplitter(infix, new LastLocator()), ResultFactoryImpl.get());
    }

    public static Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> First(final Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> leftRule,
                                                                                                                 final String infix,
                                                                                                                 final Rule<EverythingNode, NodeResult<EverythingNode, FormatError>, StringResult<FormatError>> rightRule) {
        return new SplitRule<>(leftRule, rightRule, new InfixSplitter(infix, new FirstLocator()), ResultFactoryImpl.get());
    }
}
