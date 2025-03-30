#ifndef magma_compile_lang_SymbolRule
#define magma_compile_lang_SymbolRule
#include "../../../magma/compile/CompileError.h"
#include "../../../magma/compile/Node.h"
#include "../../../magma/compile/context/StringContext.h"
#include "../../../magma/compile/rule/Rule.h"
#include "../../../magma/result/Err.h"
#include "../../../magma/result/Result.h"
struct SymbolRule{};
Result<struct Node, struct CompileError> parse(struct String input);
int isSymbol(struct String input);
Result<struct String, struct CompileError> generate(struct Node node);
#endif
