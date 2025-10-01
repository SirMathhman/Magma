struct Some<T>(T value) implements Option<T>{};
Option_? map_Some<T>(T value) implements Option<T>(R mapper) {}
Option_? flatMap_Some<T>(T value) implements Option<T>(OptionR mapper) {}
T orElse_Some<T>(T value) implements Option<T>(T other) {}
