package magma;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		// Example usage of the two-dimensional map function
		Map<List<String>, Map<String, String>> exampleMap = new HashMap<>();

		// Create inner maps
		Map<String, String> innerMap1 = new HashMap<>();
		innerMap1.put("key1", "value1");
		innerMap1.put("key2", "value2");

		Map<String, String> innerMap2 = new HashMap<>();
		innerMap2.put("keyA", "valueA");
		innerMap2.put("keyB", "valueB");

		// Add inner maps to the outer map with List<String> keys
		exampleMap.put(Arrays.asList("category1", "subcategory1"), innerMap1);
		exampleMap.put(Arrays.asList("category2", "subcategory2"), innerMap2);

		// Process the map using our stub function
		Map<List<String>, Map<String, String>> resultMap = MapUtils.processTwoDimensionalMap(exampleMap);

		// Print the result (which should be the same as the input in this stub implementation)
		System.out.println("Result map: " + resultMap);
	}
}
