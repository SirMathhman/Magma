package magma;

import static org.junit.jupiter.api.Assertions.assertEquals;

import magma.list.JdkList;
import magma.list.ListLike;
import org.junit.jupiter.api.Test;

class ListLikeTest {
    @Test
    void addsAndRetrievesValues() {
        ListLike<Integer> list = JdkList.create();
        list.add(1);
        list.add(2);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        int sum = list.iterator().fold(0, (acc, v) -> acc + v);
        assertEquals(3, sum);

        var mapped = list.iterator().map(Object::toString);
        assertEquals("1", mapped.get(0));
        assertEquals("2", mapped.get(1));
    }
}
