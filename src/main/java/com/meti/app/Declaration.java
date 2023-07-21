package com.meti.app;

import com.meti.java.String_;

public record Declaration(String_ name, String_ type) implements Renderable {
    @Override
    public String_ render() {
        return name.append(" : ").appendOwned(type);
    }
}
