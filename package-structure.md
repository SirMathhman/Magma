# Proposed Package Structure

Based on the analysis of the codebase, I'm proposing the following package structure to meet the requirement of having no more than 10 classes per package:

## Package 1: magma.core
Core compiler components and exceptions:
1. Compiler.java
2. CompileException.java
3. TypeMapper.java
4. ValueProcessor.java
5. VariableDeclaration.java

## Package 2: magma.validation
Validation components:
1. ArithmeticValidator.java
2. BooleanExpressionValidator.java
3. ComparisonValidator.java
4. OperatorChecker.java

## Package 3: magma.declaration
Declaration processing components:
1. DeclarationProcessor.java
2. DeclarationConfig.java
3. DeclarationContext.java

## Package 4: magma.params
Parameter classes:
1. ArithmeticTypeCheckParams.java
2. BinaryOperationParams.java
3. BooleanOperationParams.java
4. ComparisonValidatorParams.java
5. OperatorCheckParams.java
6. TypeCheckParams.java
7. TypeScriptAnnotationParams.java

This structure:
- Keeps related functionality together
- Ensures no package exceeds 10 classes
- Follows logical domain boundaries
- Separates parameter classes from their using classes