#include "/../../jvm/collect/list/Lists.h"
#include "/../../magma/collect/list/List_.h"
#include "/../../magma/collect/stream/Joiner.h"
#include "/../../magma/collect/stream/head/HeadedStream.h"
#include "/../../magma/collect/stream/head/RangeHead.h"
#include "/../../magma/compile/context/Context.h"
#include "/../../magma/error/Error.h"
#include "/../../java/util/function/Function.h"
struct CompileError{struct String messagestruct Context contextstruct List__CompileError errors};
struct public CompileError(struct String message, struct Context context){
}
struct public CompileError(struct String message, struct Context context, struct List__CompileError errors){
}
struct String format(struct int depth, struct int index, struct List__CompileError sorted){
}
struct int depth(){
}
struct String display(){
}
struct String format(struct int depth){
}
