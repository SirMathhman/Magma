/* Generated header for magma.util.Tuple */
#ifndef MAGMA_UTIL_TUPLE_H
#define MAGMA_UTIL_TUPLE_H

/* Original Java source:
    package magma.util;
    
    import java.util.Objects;
    
    /**
     * Simple generic tuple/pair type.
     */
    public record Tuple<L, R>(L left, R right) {
        public Tuple {
            Objects.requireNonNull(left, "left must not be null");
            Objects.requireNonNull(right, "right must not be null");
        }
    
        public static <L, R> Tuple<L, R> of(L l, R r) {
            return new Tuple<>(l, r);
        }
    }
    

*/

#endif /* MAGMA_UTIL_TUPLE_H */
