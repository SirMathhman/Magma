struct Main{};
void main_Main(char** args) {}
/*Option_?*/ run_Main() {}
/*Option_?*/ compileAllJavaFiles_Main(Path javaSourceRoot, Path cOutputRoot) {}
/*Option_?*/ compileJavaFile_Main(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {}
/*Option_?*/ writeString_Main(Path path, char* result) {}
/*ThrowableError>*/ readString_Main(Path source) {}
/*CompileError>*/ compile_Main(char* input) {}
/*CompileError>*/ transform_Main(JavaRoot node) {}
/*List_?*/ flattenRootSegment_Main(JavaRootSegment segment) {}
/*List_?*/ flattenStructure_Main(JStructure aClass) {}
/*Option_?*/ flattenStructureSegment_Main(JavaStructureSegment self, char* name) {}
Function transformMethod_Main(Method method, char* structName) {}
CDefinition transformDefinition_Main(JavaDefinition definition) {}
CType transformType_Main(JavaType type) {}
