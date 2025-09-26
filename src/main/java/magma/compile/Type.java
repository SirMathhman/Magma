package magma.compile;

import java.util.List;
import java.util.Objects;

public sealed interface Type permits Type.PrimitiveType, Type.StructType, Type.PointerType, Type.FunctionType {
	
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
	
	record PointerType(Type pointeeType) implements Type {
		public PointerType(Type pointeeType) {
			this.pointeeType = Objects.requireNonNull(pointeeType);
		}
		
		@Override
		public String toString() {
			return "*" + pointeeType;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof PointerType other)) return false;
			return Objects.equals(pointeeType, other.pointeeType);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(pointeeType);
		}
	}
	
	record FunctionType(List<Type> parameterTypes, Type returnType) implements Type {
		public FunctionType(List<Type> parameterTypes, Type returnType) {
			this.parameterTypes = List.copyOf(parameterTypes);
			this.returnType = Objects.requireNonNull(returnType);
		}
		
		@Override
		public String toString() {
			if (parameterTypes.isEmpty()) {
				return "() => " + returnType;
			}
			return "(" + String.join(", ", parameterTypes.stream().map(Type::toString).toList()) + ") => " + returnType;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof FunctionType other)) return false;
			return Objects.equals(parameterTypes, other.parameterTypes) && Objects.equals(returnType, other.returnType);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(parameterTypes, returnType);
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
