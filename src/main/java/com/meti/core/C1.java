package com.meti.core;

public interface C1<A, B extends Exception> {
    void consume(A a) throws B;
}