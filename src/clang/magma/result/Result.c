#include "Result.h"
magma.option.Option<T> findValue();
magma.option.Option<X> findError();
magma.result.Result<magma.result.R, X> mapValue(magma.result.R(*mapper)(T));
magma.result.Result<magma.result.R, X> flatMapValue(magma.result.Result<magma.result.R, X>(*mapper)(T));
magma.result.Result<T, magma.result.R> mapErr(magma.result.R(*mapper)(X));
magma.result.R match(magma.result.R(*whenOk)(T), magma.result.R(*whenErr)(X));
magma.result.Result<magma.option.Tuple<T, magma.result.R>, X> and(magma.result.Result<magma.result.R, X>(*other)());
magma.result.void consume(void(*whenOk)(T), void(*whenErr)(X));

