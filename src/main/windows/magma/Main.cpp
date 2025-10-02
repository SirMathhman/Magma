// Generated transpiled C++ from 'src\main\java\magma\Main.java'. This file shouldn't be edited, and rather the compiler implementation should be changed.
struct Main{};
void main_Main(char** args) {}
Option<ApplicationError> run_Main() {}
Option<ApplicationError> compileAllJavaFiles_Main(Path javaSourceRoot, Path cOutputRoot) {}
Option<ApplicationError> compileJavaFile_Main(Path javaFile, Path javaSourceRoot, Path cOutputRoot) {}
Option<IOException> writeString_Main(Path path, char* result) {}
Result<String, ThrowableError> readString_Main(Path source) {}
Result<String, CompileError> compile_Main(char* input) {}
Result<CRoot, CompileError> transform_Main(JavaRoot node) {}
List<CRootSegment> flattenRootSegment_Main(JavaRootSegment segment) {}
List<CRootSegment> flattenStructure_Main(JStructure aClass) {}
Tuple<List<CRootSegment>, Option<CDefinition>> flattenStructureSegment_Main(JStructureSegment self, char* name) {}
Function transformMethod_Main(Method method, char* structName) {}
CParameter transformParameter_Main(JavaDefinition param) {}
Option<List<Identifier>> extractMethodTypeParameters_Main(Method method) {}
void collectTypeVariables_Main(JavaType type, Set<String> typeVars) {}
CDefinition transformDefinition_Main(JavaDefinition definition) {}
CType transformType_Main(JavaType type) {}
