import magma.list.HeadedStream;
import magma.list.List;
import magma.list.RangeHead;
import magma.list.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for HeadedStream.flatMap functionality.
 * Tests various scenarios including empty streams, single elements,
 * multiple elements, and nested flattening.
 */
public class HeadedStreamFlatMapTest {

	@Test
	public void testFlatMapWithEmptyOuterStream() {
		System.out.println("=== Testing flatMap with empty outer stream ===");

		Stream<Integer> emptyStream = new HeadedStream<Integer>(new RangeHead(0));
		Stream<String> result = emptyStream.flatMap(i -> Stream.range(0, i).map(Object::toString));
		List<String> resultList = result.toList();

		assertTrue(resultList.isEmpty(), "FlatMap on empty stream should return empty list");
		System.out.println("✅ Empty outer stream test passed");
	}

	@Test
	public void testFlatMapWithEmptyInnerStreams() {
		System.out.println("\n=== Testing flatMap with empty inner streams ===");

		// Each element i maps to an empty stream (range from i to i, which is empty)
		Stream<Integer> stream = Stream.range(1, 4);
		Stream<Integer> result = stream.flatMap(i -> Stream.range(i, i));
		List<Integer> resultList = result.toList();

		assertTrue(resultList.isEmpty(), "FlatMap with all empty inner streams should return empty list");
		System.out.println("✅ Empty inner streams test passed");
	}

	@Test
	public void testFlatMapBasicFlattening() {
		System.out.println("\n=== Testing flatMap basic flattening ===");

		// Create a stream of integers: [1, 2, 3]
		// Each integer i is mapped to a range [0..i), so:
		// 1 -> [0]
		// 2 -> [0, 1]
		// 3 -> [0, 1, 2]
		// Expected flattened result: [0, 0, 1, 0, 1, 2]
		Stream<Integer> stream = Stream.range(1, 4);
		Stream<Integer> flattened = stream.flatMap(i -> Stream.range(0, i));
		List<Integer> resultList = flattened.toList();

		System.out.println("Input: [1, 2, 3]");
		System.out.println("Each i mapped to range [0..i)");
		System.out.println("Result: " + resultList);

		assertEquals(6, resultList.size(), "Should have 6 elements (1 + 2 + 3)");
		assertEquals(0, resultList.getOrNull(0), "First element should be 0");
		assertEquals(0, resultList.getOrNull(1), "Second element should be 0");
		assertEquals(1, resultList.getOrNull(2), "Third element should be 1");
		assertEquals(0, resultList.getOrNull(3), "Fourth element should be 0");
		assertEquals(1, resultList.getOrNull(4), "Fifth element should be 1");
		assertEquals(2, resultList.getOrNull(5), "Sixth element should be 2");

		System.out.println("✅ Basic flattening test passed");
	}

	@Test
	public void testFlatMapWithSingleElement() {
		System.out.println("\n=== Testing flatMap with single element ===");

		Stream<Integer> stream = Stream.range(5, 6);
		Stream<String> result = stream.flatMap(i -> Stream.range(0, i).map(j -> "item-" + j));
		List<String> resultList = result.toList();

		assertEquals(5, resultList.size(), "Should have 5 elements");
		assertEquals("item-0", resultList.getOrNull(0));
		assertEquals("item-1", resultList.getOrNull(1));
		assertEquals("item-2", resultList.getOrNull(2));
		assertEquals("item-3", resultList.getOrNull(3));
		assertEquals("item-4", resultList.getOrNull(4));

		System.out.println("✅ Single element test passed");
	}

	@Test
	public void testFlatMapWithMixedEmptyAndNonEmpty() {
		System.out.println("\n=== Testing flatMap with mixed empty and non-empty streams ===");

		// Create a list: [0, 1, 0, 2]
		// 0 -> []
		// 1 -> [0]
		// 0 -> []
		// 2 -> [0, 1]
		// Expected: [0, 0, 1]
		List<Integer> input = List.of(0, 1, 0, 2);
		Stream<Integer> result = input.stream().flatMap(i -> Stream.range(0, i));
		List<Integer> resultList = result.toList();

		System.out.println("Input: [0, 1, 0, 2]");
		System.out.println("Result: " + resultList);

		assertEquals(3, resultList.size(), "Should have 3 elements (0 + 1 + 0 + 2)");
		assertEquals(0, resultList.getOrNull(0));
		assertEquals(0, resultList.getOrNull(1));
		assertEquals(1, resultList.getOrNull(2));

		System.out.println("✅ Mixed empty/non-empty test passed");
	}

	@Test
	public void testFlatMapChaining() {
		System.out.println("\n=== Testing flatMap chaining ===");

		// Chain multiple flatMaps
		Stream<Integer> stream = Stream.range(1, 3);
		Stream<String> result = stream
				.flatMap(i -> Stream.range(0, i))
				.flatMap(j -> Stream.range(0, j + 1))
				.map(k -> "val-" + k);

		List<String> resultList = result.toList();

		System.out.println("Chained flatMap result size: " + resultList.size());
		System.out.println("First few elements: " + resultList.getOrNull(0) + ", "
				+ resultList.getOrNull(1) + ", " + resultList.getOrNull(2));

		assertTrue(resultList.size() > 0, "Should have elements after chained flatMap");
		System.out.println("✅ Chaining test passed");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFlatMapWithListOf() {
		System.out.println("\n=== Testing flatMap with List.of ===");

		// Use List.of to create lists and flatten them
		List<Integer> list1 = List.of(1, 2);
		List<Integer> list2 = List.of(3, 4, 5);
		List<List<Integer>> listOfLists = List.of(list1, list2);

		Stream<Integer> flattened = listOfLists.stream().flatMap(List::stream);
		List<Integer> resultList = flattened.toList();

		System.out.println("Input: [[1, 2], [3, 4, 5]]");
		System.out.println("Flattened result: " + resultList);

		assertEquals(5, resultList.size(), "Should have 5 elements");
		assertEquals(1, resultList.getOrNull(0));
		assertEquals(2, resultList.getOrNull(1));
		assertEquals(3, resultList.getOrNull(2));
		assertEquals(4, resultList.getOrNull(3));
		assertEquals(5, resultList.getOrNull(4));

		System.out.println("✅ List.of flattening test passed");
	}

	@Test
	public void testFlatMapPreservesOrder() {
		System.out.println("\n=== Testing flatMap preserves order ===");

		Stream<String> stream = Stream.range(1, 4).map(i -> "outer-" + i);
		Stream<String> flattened = stream.flatMap(s -> Stream.range(1, 3).map(i -> s + "-inner-" + i));

		List<String> resultList = flattened.toList();

		System.out.println("Result: " + resultList);

		assertEquals(6, resultList.size());
		assertEquals("outer-1-inner-1", resultList.getOrNull(0));
		assertEquals("outer-1-inner-2", resultList.getOrNull(1));
		assertEquals("outer-2-inner-1", resultList.getOrNull(2));
		assertEquals("outer-2-inner-2", resultList.getOrNull(3));
		assertEquals("outer-3-inner-1", resultList.getOrNull(4));
		assertEquals("outer-3-inner-2", resultList.getOrNull(5));

		System.out.println("✅ Order preservation test passed");
	}
}
