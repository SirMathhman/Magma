/**
 * Record to hold multi-dimensional array declaration information.
 * This extends the concept of ArrayDeclaration to support multiple dimensions.
 * The dimensions array contains all the sizes in order.
 */
record MultiDimArrayDeclaration(String name, String type, int[] dimensions, String elements) {}
