#include "LazyRule.h"
struct Result_Node_CompileError parse(struct String input){return withChildSet(inner -> inner.parse(input), new StringContext(input));
}
struct Result_String_CompileError generate(struct Node node){return withChildSet(inner -> inner.generate(node), new NodeContext(node));
}
struct Result_T_CompileError withChildSet(struct Result_T_CompileError(*mapper)(struct Rule), struct Context context){return child.map(mapper).orElseGet(() -> new Err<>(new CompileError(, context)));
}
struct void set(struct Rule child){this.child = new Some<>(child);
}

