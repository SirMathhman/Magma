struct Ok<T, X>(T value) implements Result<T, X>{};
X> mapValue_Ok<T, X>(T value) implements Result<T, X>() {}
X> flatMap_Ok<T, X>(T value) implements Result<T, X>() {}
R> mapErr_Ok<T, X>(T value) implements Result<T, X>() {}
