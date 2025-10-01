struct Err<T, X>(X error) implements Result<T, X>{};
X> mapValue_Err<T, X>(X error) implements Result<T, X>() {}
X> flatMap_Err<T, X>(X error) implements Result<T, X>() {}
R> mapErr_Err<T, X>(X error) implements Result<T, X>() {}
