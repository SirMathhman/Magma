struct Main{};
void main_Main(/*String[]*/ args) {}
/*Option_?*/ run_Main() {}
/*Option_?*/ compileAllJavaFiles_Main(Path javaSourceRoot, Path cOutputRoot) {}
/*Option_?*/ compileJavaFile_Main(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {}
/*Option_?*/ writeString_Main(Path path, String result) {}
/*ThrowableError>*/ readString_Main(Path source) {}
/*CompileError>*/ compile_Main(String input) {}
/*CompileError>*/ transform_Main(JavaRoot node) {}
/*List_?*/ flattenRootSegment_Main(JavaRootSegment segment) {}
/*List_?*/ flattenStructure_Main(JStructure aClass) {}
/*Option_?*/ flattenStructureSegment_Main(JavaStructureSegment self, String name) {}
Function transformMethod_Main(Method method, String structName) {}
CDefinition transformDefinition_Main(JavaDefinition definition) {}
CType transformType_Main(JavaType type) {}
