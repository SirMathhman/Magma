struct Some<T>(T value) implements Option<T> {};
Option<R> map_Some<T>(T value) implements Option<T> 
Option<R> flatMap_Some<T>(T value) implements Option<T> 
T orElse_Some<T>(T value) implements Option<T> 
