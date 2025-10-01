struct Some<T>(T value) implements Option<T>{};
Option_? map_Some<T>(T value) implements Option<T>() {}
Option_? flatMap_Some<T>(T value) implements Option<T>() {}
T orElse_Some<T>(T value) implements Option<T>() {}
