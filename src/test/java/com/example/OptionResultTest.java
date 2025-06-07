package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.option.None;
import com.example.option.Option;
import com.example.option.Some;
import com.example.result.Err;
import com.example.result.Ok;
import com.example.result.Result;
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
