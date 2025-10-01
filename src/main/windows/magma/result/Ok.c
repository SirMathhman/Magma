struct Ok<T, X>(T value) implements Result<T, X>{};
X> mapValue_Ok<T, X>(T value) implements Result<T, X>(R fn) {}
X> flatMap_Ok<T, X>(T value) implements Result<T, X>(X fn) {}
R> mapErr_Ok<T, X>(T value) implements Result<T, X>(R mapper) {}
