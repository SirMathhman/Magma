#include "Stream.h"
magma.collect.stream.R foldWithInitial(magma.collect.stream.R initial, magma.collect.stream.R(*folder)(magma.collect.stream.R, T));
magma.collect.stream.Stream<magma.collect.stream.R> map(magma.collect.stream.R(*mapper)(T));
magma.collect.stream.C collect(magma.collect.stream.Collector<T, magma.collect.stream.C> collector);
magma.collect.stream.Stream<magma.collect.stream.R> flatMap(magma.collect.stream.Stream<magma.collect.stream.R>(*mapper)(T));
magma.result.Result<magma.collect.stream.R, magma.collect.stream.X> foldToResult(magma.collect.stream.R initial, magma.result.Result<magma.collect.stream.R, magma.collect.stream.X>(*folder)(magma.collect.stream.R, T));
magma.option.Option<magma.collect.stream.R> foldMapping(magma.collect.stream.R(*mapper)(T), magma.collect.stream.R(*folder)(magma.collect.stream.R, T));
magma.collect.stream.Stream<T> filter(int(*predicate)(T));
magma.collect.stream.Stream<T> concat(magma.collect.stream.Stream<T> other);
magma.option.Option<T> next();
magma.collect.stream.boolean anyMatch(int(*predicate)(T));

