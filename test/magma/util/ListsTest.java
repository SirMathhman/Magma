package magma.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ListsTest {
    @Test
    void ofCreatesList() {
        List<String> list = Lists.of("a", "b");
        assertFalse(list.isEmpty());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
    }
}
