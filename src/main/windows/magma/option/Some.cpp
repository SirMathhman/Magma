// Generated transpiled C++ from 'src\main\java\magma\option\Some.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
template<typename T>
struct Some{T value;};
template<typename R, typename T>
Option<R> map_Some(R (*mapper)(T)) {}
template<typename R, typename T>
Option<R> flatMap_Some(Option<R> (*mapper)(T)) {}
template<typename T>
T orElse_Some(T other) {}
