package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OptionResultTest {
    @Test
    void someAndNoneAreDistinctClasses() {
        Option<Integer> some = new Some<>(1);
        Option<Integer> none = new None<>();
        assertTrue(some instanceof Some);
        assertTrue(none instanceof None);
    }

    @Test
    void okAndErrAreDistinctClasses() {
        Result<Integer> ok = new Ok<>(1);
        Result<Integer> err = new Err<>("bad");
        assertTrue(ok instanceof Ok);
        assertTrue(err instanceof Err);
    }
}
