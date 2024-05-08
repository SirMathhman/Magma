package com.meti;

import java.util.Optional;

record Definition(String name, Optional<String> type, Optional<String> value) {
    String render() {
        var typeString = type.map(type -> " : " + type).orElse("");
        var valueString = value.map(value -> " = " + value).orElse("");
        return "let " + name + typeString + valueString;
    }
}
