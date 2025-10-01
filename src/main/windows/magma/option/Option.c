struct Option<T> permits Some, None{};
Option_? of_Option<T> permits Some, None(T value) {}
Option_? empty_Option<T> permits Some, None() {}
Option_? ofNullable_Option<T> permits Some, None(T value) {}
