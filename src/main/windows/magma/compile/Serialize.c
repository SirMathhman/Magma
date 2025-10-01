struct Serialize{};
/*CompileError>*/ deserialize_Serialize(ClassT clazz, Node node) {}
/*CompileError>*/ serialize_Serialize(ClassT clazz, T node) {}
/*Option_?*/ resolveTypeIdentifier_Serialize(/*Class?*/ clazz) {}
/*CompileError>*/ deserializeSealed_Serialize(ClassT clazz, Node node) {}
/*CompileError>*/ deserializeComponent_Serialize(RecordComponent component, Node node) {}
/*CompileError>*/ deserializeOptionalComponent_Serialize(RecordComponent component, Node node) {}
/*CompileError>*/ deserializeListComponent_Serialize(RecordComponent component, Node node) {}
/*CompileError>*/ deserializeNestedComponent_Serialize(RecordComponent component, Node node) {}
/*CompileError>*/ deserializeRaw_Serialize(/*Class?*/ type, Node node) {}
/*CompileError>*/ serializeRaw_Serialize(/*Class?*/ clazz, Object value) {}
CompileError missingFieldError_Serialize(String key, /* Class?*/ type, Node node) {}
/*Option_?*/ findStringInChildren_Serialize(Node node, String key) {}
/*Option_?*/ writeComponent_Serialize(Node target, RecordComponent component, Object value) {}
/*Option_?*/ writeOptionalComponent_Serialize(Node target, RecordComponent component, Object value) {}
/*Option_?*/ writeListComponent_Serialize(Node target, RecordComponent component, Object value) {}
/*Class_?*/ erase_Serialize(Type type) {}
boolean shouldBeDeserializableAs_Serialize(Node node, /* Class?*/ targetClass) {}
