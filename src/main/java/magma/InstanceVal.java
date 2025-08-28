package magma;

import java.util.Map;

public record InstanceVal(String typeName, Map<String, Value> fields) implements Value {}
