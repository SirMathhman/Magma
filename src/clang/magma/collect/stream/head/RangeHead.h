#ifndef magma_collect_stream_head_RangeHead
#define magma_collect_stream_head_RangeHead
#include "../../../../magma/option/None.h"
#include "../../../../magma/option/Option.h"
#include "../../../../magma/option/Some.h"
struct RangeHead{int extent;int counter;
};
// expand Option_Integer = Option<struct Integer>
// expand Some_ = Some<struct >
struct public RangeHead(int extent);
struct Option_Integer next();
#endif

