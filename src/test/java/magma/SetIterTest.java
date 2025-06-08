package magma;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashSet;
import java.util.Set;

import magma.list.SetIter;
import magma.list.Iter;
import magma.list.JdkList;
import magma.list.ListLike;
import org.junit.jupiter.api.Test;

class SetIterTest {
    @Test
    void iteratesOverSetValues() {
        Set<Integer> set = new LinkedHashSet<>();
        set.add(1);
        set.add(2);
        Iter<Integer> iter = SetIter.wrap(set);
        ListLike<Integer> list = iter.fold(JdkList.create(), (acc, v) -> { acc.add(v); return acc; });
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }
}
