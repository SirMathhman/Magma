struct None<T> implements Option<T>{};
/*Option_?*/ map_None<T> implements Option<T>(R mapper) {}
/*Option_?*/ flatMap_None<T> implements Option<T>(OptionR mapper) {}
T orElse_None<T> implements Option<T>(T other) {}
