#ifndef magma_compile_rule_tree_OrRule
#define magma_compile_rule_tree_OrRule
#include "../../../../magma/collect/list/List_.h"
#include "../../../../magma/compile/CompileError.h"
#include "../../../../magma/compile/Node.h"
#include "../../../../magma/compile/context/Context.h"
#include "../../../../magma/compile/context/NodeContext.h"
#include "../../../../magma/compile/context/StringContext.h"
#include "../../../../magma/compile/rule/Rule.h"
#include "../../../../magma/result/Err.h"
#include "../../../../magma/result/Ok.h"
#include "../../../../magma/result/Result.h"
struct OrRule{};
struct Result_T_CompileError apply(struct Function_Rule_Result_T_CompileError applicator, struct Supplier_Context context);
struct Result_Node_CompileError parse(struct String input);
struct Result_String_CompileError generate(struct Node input);
#endif
