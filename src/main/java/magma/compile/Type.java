package magma.compile;

import java.util.List;
import java.util.Objects;

public sealed interface Type permits Type.PrimitiveType, Type.StructType {
	
	record PrimitiveType(String name) implements Type {
		public static final PrimitiveType I32 = new PrimitiveType("I32");
		public static final PrimitiveType BOOL = new PrimitiveType("BOOL");
		public static final PrimitiveType VOID = new PrimitiveType("VOID");
		
		public boolean isNonNumeric() {
			return this != I32;
		}
		
		public boolean isNonBoolean() {
			return this != BOOL;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	record StructType(String name, List<StructField> fields) implements Type {
		public StructType(String name, List<StructField> fields) {
			this.name = Objects.requireNonNull(name);
			this.fields = List.copyOf(fields);
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	record StructField(String name, Type type) {
		public StructField(String name, Type type) {
			this.name = Objects.requireNonNull(name);
			this.type = Objects.requireNonNull(type);
		}
	}
	
	// Static references for compatibility
	Type I32 = PrimitiveType.I32;
	Type BOOL = PrimitiveType.BOOL;
	Type VOID = PrimitiveType.VOID;
	
	default boolean isNonNumeric() {
		return this != I32;
	}
	
	default boolean isNonBoolean() {
		return this != BOOL;
	}
}
