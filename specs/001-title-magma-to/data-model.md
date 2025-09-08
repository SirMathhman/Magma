# Data Model: Magma → C compiler (Phase 1)

## Overview
This document describes the internal data shapes used by the compiler: AST nodes, symbol table entries, and codegen units.

## AST (abstract shapes)
- Program: list of Module or Top-level Declarations
- Module: name, list of Declarations
- Declaration: FunctionDecl | VarDecl | TypeDecl
- FunctionDecl: name, parameters[], returnType, body(Block)
- VarDecl: name, type, optional initializer(Expression)
- Block: list of Statements
- Statement: IfStmt | WhileStmt | ReturnStmt | ExprStmt | VarDecl
- Expression: BinaryOp(left, op, right) | UnaryOp | Literal | Identifier | Call
- Literal: IntLiteral, FloatLiteral, BoolLiteral, StringLiteral

## Symbol Table
- Scope stack mapping identifier -> SymbolEntry
- SymbolEntry: name, kind (variable/function/type), typeInfo, definedAt (file,line)

## Type System (initial)
- Primitive: int, float, bool, string
- Function types: parameter types list + return type
- Type checking: simple structural checks for initial subset

## Codegen Unit
- TranslationUnit: maps module -> generated C file
- Each TranslationUnit contains:
  - includes: list of required headers
  - forward declarations
  - function definitions
  - helper runtime glue (if needed)

## Notes
- Data model kept intentionally minimal for Phase 1; additional nodes added as language features expand.
