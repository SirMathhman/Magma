#ifndef magma_compile_lang_Transformers
#define magma_compile_lang_Transformers
#include "../../../magma/collect/list/List_.h"
#include "../../../magma/compile/CompileError.h"
#include "../../../magma/compile/Node.h"
#include "../../../magma/compile/context/NodeContext.h"
#include "../../../magma/result/Err.h"
#include "../../../magma/result/Ok.h"
#include "../../../magma/result/Result.h"
struct Transformers{
};
// expand magma.result.Result<magma.compile.Node, magma.compile.CompileError>
// expand magma.result.Result<magma.compile.Node, magma.compile.CompileError>
// expand magma.result.Result<magma.collect.list.List_<magma.compile.Node>, magma.compile.CompileError>
// expand magma.collect.list.List_<magma.compile.Node>
// expand magma.collect.list.List_<magma.compile.Node>
// expand magma.result.Result<magma.collect.list.List_<magma.compile.Node>, magma.compile.CompileError>
// expand magma.collect.list.List_<magma.compile.Node>
// expand magma.collect.list.List_<magma.compile.Node>
// expand magma.result.Err<>
// expand magma.result.Err<>
int __lambda0__();
int __lambda1__();
magma.result.Result<magma.compile.Node, magma.compile.CompileError> findNode(magma.compile.Node node, String propertyKey);
magma.result.Result<magma.collect.list.List_<magma.compile.Node>, magma.compile.CompileError> findNodeList(magma.compile.Node value, String propertyKey);
#endif
