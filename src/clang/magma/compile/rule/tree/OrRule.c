#include "OrRule.h"
magma.result.Result<magma.compile.rule.tree.T, magma.compile.CompileError> apply(magma.result.Result<magma.compile.rule.tree.T, magma.compile.CompileError>(*applicator)(magma.compile.rule.Rule), magma.compile.context.Context(*context)()){return rules.stream().foldWithInitial((), __lambda0__.apply(rule).match(orState.withValue, orState.withError)).toResult().match(Ok.new, __lambda1__);
}
magma.result.Result<magma.compile.Node, magma.compile.CompileError> parse(String input){return apply(__lambda2__.parse(input), __lambda3__);
}
magma.result.Result<String, magma.compile.CompileError> generate(magma.compile.Node input){return apply(__lambda4__.generate(input), __lambda5__);
}
auto __lambda0__();
auto __lambda1__();
auto __lambda2__();
auto __lambda3__();
auto __lambda4__();
auto __lambda5__();

