#include "TypeRule.h"
expand Result_Node_CompileError
expand Result_String_CompileError
struct Result_Node_CompileError parse(struct String input}{return rule.parse(input)
                .mapValue(node -> node.retype(type))
                .mapErr(error -> new CompileError( + type + , new StringContext(input), Lists.of(error)));}struct Result_String_CompileError generate(struct Node node}{if (node.is(type)) {
            return rule.generate(node).mapErr(err -> new CompileError( + type + , new NodeContext(node), Lists.of(err)));
        }

        return new Err<>(new CompileError( + type + , new NodeContext(node)));}