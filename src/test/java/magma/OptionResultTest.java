package magma;

import static org.junit.jupiter.api.Assertions.*;

import magma.option.None;
import magma.option.Option;
import magma.option.Some;
import magma.list.Iter;
import magma.result.Err;
import magma.result.Ok;
import magma.result.Result;
import org.junit.jupiter.api.Test;

class OptionResultTest {
    @Test
    void someAndNoneAreDistinctClasses() {
        Option<Integer> some = new Some<>(1);
        Option<Integer> none = new None<>();
        assertInstanceOf(Some.class, some);
        assertInstanceOf(None.class, none);
    }

    @Test
    void okAndErrAreDistinctClasses() {
        Result<Integer> ok = new Ok<>(1);
        Result<Integer> err = new Err<>("bad");
        assertInstanceOf(Ok.class, ok);
        assertInstanceOf(Err.class, err);
    }

    @Test
    void optionConvertsToIter() {
        Option<Integer> some = new Some<>(7);
        Iter<Integer> iter = some.toIter();
        assertTrue(iter.hasNext());
        assertEquals(7, iter.next());
        assertFalse(iter.hasNext());

        Option<Integer> none = new None<>();
        Iter<Integer> empty = none.toIter();
        assertFalse(empty.hasNext());
    }
}
