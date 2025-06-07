package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OptionResultTest {
    @Test
    void someAndNoneAreDistinctClasses() {
        Option<Integer> some = Option.some(1);
        Option<Integer> none = Option.none();
        assertTrue(some instanceof Some);
        assertTrue(none instanceof None);
    }

    @Test
    void okAndErrAreDistinctClasses() {
        Result<Integer> ok = Result.ok(1);
        Result<Integer> err = Result.error("bad");
        assertTrue(ok instanceof Ok);
        assertTrue(err instanceof Err);
    }
}
