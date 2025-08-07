/**
 * Record to hold array declaration information.
 * This eliminates the need to pass around multiple related parameters.
 * Supports both single and multi-dimensional arrays.
 * For single-dimensional arrays, the dimensions array will have a single element.
 * For multi-dimensional arrays, the dimensions array will contain all the sizes in order.
 */
record ArrayDeclaration(String name, String type, int[] dimensions, String elements) {
    /**
     * Convenience constructor for single-dimensional arrays.
     */
    public ArrayDeclaration(String name, String type, int size, String elements) {
        this(name, type, new int[]{size}, elements);
    }
}
