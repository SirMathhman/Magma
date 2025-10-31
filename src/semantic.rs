/// Semantic Analysis for Magma
///
/// Phase: Symbol table, type checking, borrow checking, monomorphization
/// Weeks 3-4 Implementation
use crate::ast::*;
use crate::diagnostics::CompilationError;
use crate::token::Span;
use std::collections::HashMap;

/// Resolved type with ownership information
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ResolvedType {
    I32,
    I64,
    F32,
    F64,
    String,
    Bool,
    Void,
    Reference(Box<ResolvedType>),
    MutableReference(Box<ResolvedType>),
    Struct(String),
    Generic(String, Vec<ResolvedType>),
    Array(Box<ResolvedType>),
}

impl ResolvedType {
    pub fn is_copy(&self) -> bool {
        matches!(
            self,
            ResolvedType::I32
                | ResolvedType::I64
                | ResolvedType::F32
                | ResolvedType::F64
                | ResolvedType::Bool
        )
    }

    pub fn is_numeric(&self) -> bool {
        matches!(
            self,
            ResolvedType::I32 | ResolvedType::I64 | ResolvedType::F32 | ResolvedType::F64
        )
    }

    pub fn is_integer(&self) -> bool {
        matches!(self, ResolvedType::I32 | ResolvedType::I64)
    }

    pub fn is_boolean(&self) -> bool {
        matches!(self, ResolvedType::Bool)
    }
}

impl std::fmt::Display for ResolvedType {
    fn fmt(&self, f: &mut std::fmt::Formatter) -> std::fmt::Result {
        match self {
            ResolvedType::I32 => write!(f, "I32"),
            ResolvedType::I64 => write!(f, "I64"),
            ResolvedType::F32 => write!(f, "F32"),
            ResolvedType::F64 => write!(f, "F64"),
            ResolvedType::String => write!(f, "String"),
            ResolvedType::Bool => write!(f, "Bool"),
            ResolvedType::Void => write!(f, "Void"),
            ResolvedType::Reference(t) => write!(f, "&{}", t),
            ResolvedType::MutableReference(t) => write!(f, "&mut {}", t),
            ResolvedType::Struct(name) => write!(f, "{}", name),
            ResolvedType::Generic(name, args) => {
                write!(f, "{}<", name)?;
                for (i, arg) in args.iter().enumerate() {
                    if i > 0 {
                        write!(f, ", ")?;
                    }
                    write!(f, "{}", arg)?;
                }
                write!(f, ">")
            }
            ResolvedType::Array(t) => write!(f, "[{}]", t),
        }
    }
}

/// Information about a function
#[derive(Debug, Clone)]
pub struct FunctionInfo {
    pub name: String,
    pub param_types: Vec<ResolvedType>,
    pub return_type: ResolvedType,
}

/// Information about a struct
#[derive(Debug, Clone)]
pub struct StructInfo {
    pub name: String,
    pub fields: HashMap<String, ResolvedType>,
}

/// Symbol table for managing definitions
#[derive(Debug, Clone)]
pub struct SymbolTable {
    functions: HashMap<String, FunctionInfo>,
    structs: HashMap<String, StructInfo>,
    types: HashMap<String, ResolvedType>,
}

impl SymbolTable {
    pub fn new() -> Self {
        SymbolTable {
            functions: HashMap::new(),
            structs: HashMap::new(),
            types: HashMap::new(),
        }
    }

    pub fn add_function(&mut self, info: FunctionInfo) -> Result<(), CompilationError> {
        if self.functions.contains_key(&info.name) {
            return Err(CompilationError::error(
                format!("Function '{}' already defined", info.name),
                Span::new(0, 0, 0, 0),
                "",
            ));
        }
        self.functions.insert(info.name.clone(), info);
        Ok(())
    }

    pub fn add_struct(&mut self, info: StructInfo) -> Result<(), CompilationError> {
        if self.structs.contains_key(&info.name) {
            return Err(CompilationError::error(
                format!("Struct '{}' already defined", info.name),
                Span::new(0, 0, 0, 0),
                "",
            ));
        }
        self.structs.insert(info.name.clone(), info);
        Ok(())
    }

    pub fn get_function(&self, name: &str) -> Option<&FunctionInfo> {
        self.functions.get(name)
    }

    pub fn get_struct(&self, name: &str) -> Option<&StructInfo> {
        self.structs.get(name)
    }
}

/// Type checker for Magma programs
pub struct TypeChecker {
    symbol_table: SymbolTable,
    local_scope: HashMap<String, ResolvedType>,
    errors: Vec<CompilationError>,
}

impl TypeChecker {
    pub fn new() -> Self {
        TypeChecker {
            symbol_table: SymbolTable::new(),
            local_scope: HashMap::new(),
            errors: Vec::new(),
        }
    }

    /// Check a complete program
    pub fn check_program(
        mut self,
        program: &Program,
    ) -> Result<SymbolTable, Vec<CompilationError>> {
        // First pass: collect all function and struct definitions
        for item in &program.items {
            match item {
                Item::FunctionDef(func) => {
                    if let Err(e) = self.collect_function(func) {
                        self.errors.push(e);
                    }
                }
                Item::StructDef(struct_def) => {
                    if let Err(e) = self.collect_struct(struct_def) {
                        self.errors.push(e);
                    }
                }
                Item::ClassDef(class_def) => {
                    if let Err(e) = self.collect_class(class_def) {
                        self.errors.push(e);
                    }
                }
            }
        }

        if !self.errors.is_empty() {
            return Err(self.errors);
        }

        // Second pass: type check function bodies
        for item in &program.items {
            if let Item::FunctionDef(func) = item {
                if let Err(e) = self.check_function_body(func) {
                    self.errors.push(e);
                }
            }
        }

        if !self.errors.is_empty() {
            return Err(self.errors);
        }

        Ok(self.symbol_table)
    }

    fn collect_function(&mut self, func: &FunctionDef) -> Result<(), CompilationError> {
        let param_types: Result<Vec<_>, _> = func
            .params
            .iter()
            .map(|p| self.resolve_type(&p.ty))
            .collect();

        let param_types = param_types?;
        let return_type = self.resolve_type(&func.return_type)?;

        let info = FunctionInfo {
            name: func.name.clone(),
            param_types,
            return_type,
        };

        self.symbol_table.add_function(info)
    }

    fn collect_struct(&mut self, struct_def: &StructDef) -> Result<(), CompilationError> {
        let mut fields = HashMap::new();
        for field in &struct_def.fields {
            let ty = self.resolve_type(&field.ty)?;
            fields.insert(field.name.clone(), ty);
        }

        let info = StructInfo {
            name: struct_def.name.clone(),
            fields,
        };

        self.symbol_table.add_struct(info)
    }

    fn collect_class(&mut self, class_def: &ClassDef) -> Result<(), CompilationError> {
        self.collect_function(&class_def.constructor)
    }

    fn check_function_body(&mut self, func: &FunctionDef) -> Result<(), CompilationError> {
        let return_type = self.resolve_type(&func.return_type)?;

        // Set up local scope with parameters
        self.local_scope.clear();
        for param in &func.params {
            let param_type = self.resolve_type(&param.ty)?;
            self.local_scope.insert(param.name.clone(), param_type);
        }

        let expr_type = self.infer_expression(&func.body)?;

        // Check if body type matches return type
        if !self.types_compatible(&expr_type, &return_type) {
            return Err(CompilationError::error(
                format!(
                    "Function returns {} but body has type {}",
                    return_type, expr_type
                ),
                func.body.span(),
                "",
            ));
        }

        Ok(())
    }

    fn resolve_type(&self, ty: &Type) -> Result<ResolvedType, CompilationError> {
        match ty {
            Type::Named(name) => match name.as_str() {
                "I32" => Ok(ResolvedType::I32),
                "I64" => Ok(ResolvedType::I64),
                "F32" => Ok(ResolvedType::F32),
                "F64" => Ok(ResolvedType::F64),
                "String" => Ok(ResolvedType::String),
                "Bool" => Ok(ResolvedType::Bool),
                "Void" => Ok(ResolvedType::Void),
                _ => {
                    // Check if it's a user-defined struct
                    if self.symbol_table.get_struct(name).is_some() {
                        Ok(ResolvedType::Struct(name.clone()))
                    } else {
                        Err(CompilationError::error(
                            format!("Unknown type '{}'", name),
                            Span::new(0, 0, 0, 0),
                            "",
                        ))
                    }
                }
            },
            Type::Reference(inner) => {
                Ok(ResolvedType::Reference(Box::new(self.resolve_type(inner)?)))
            }
            Type::MutableReference(inner) => Ok(ResolvedType::MutableReference(Box::new(
                self.resolve_type(inner)?,
            ))),
            Type::Generic(name, args) => {
                let resolved_args: Result<Vec<_>, _> =
                    args.iter().map(|arg| self.resolve_type(arg)).collect();
                Ok(ResolvedType::Generic(name.clone(), resolved_args?))
            }
            Type::Tuple(types) => {
                let resolved: Result<Vec<_>, _> =
                    types.iter().map(|ty| self.resolve_type(ty)).collect();
                // For now, just return the first type
                resolved?.first().cloned().ok_or_else(|| {
                    CompilationError::error("Empty tuple type", Span::new(0, 0, 0, 0), "")
                })
            }
            Type::Array(inner) => Ok(ResolvedType::Array(Box::new(self.resolve_type(inner)?))),
        }
    }

    fn infer_expression(&self, expr: &Expression) -> Result<ResolvedType, CompilationError> {
        match expr {
            Expression::Literal(lit, _) => self.infer_literal(lit),
            Expression::Identifier(name, span) => {
                // Check local scope first
                if let Some(ty) = self.local_scope.get(name) {
                    Ok(ty.clone())
                } else {
                    Err(CompilationError::error(
                        format!("Unknown variable '{}'", name),
                        *span,
                        "",
                    ))
                }
            }
            Expression::Binary(op, left, right, span) => {
                self.infer_binary_op(*op, left, right, *span)
            }
            Expression::Unary(op, expr, span) => self.infer_unary_op(*op, expr, *span),
            Expression::Call(func_expr, args, span) => {
                if let Expression::Identifier(func_name, _) = &**func_expr {
                    // Special handling for let bindings
                    if func_name == "_let_binding" {
                        // _let_binding is a pseudo-function used by the parser for let statements
                        // It takes 2 args: (variable_name, value_expr)
                        if args.len() == 2 {
                            // Just infer the type of the value expression
                            // The variable binding is handled at runtime
                            self.infer_expression(&args[1])
                        } else {
                            Err(CompilationError::error(
                                "Invalid let binding construct".to_string(),
                                *span,
                                "",
                            ))
                        }
                    } else {
                        self.infer_call(func_name, args, *span)
                    }
                } else {
                    Err(CompilationError::error(
                        "Complex function calls not yet supported",
                        *span,
                        "",
                    ))
                }
            }
            Expression::FieldAccess(_, _, span) => Err(CompilationError::error(
                "Field access type inference not yet implemented",
                *span,
                "",
            )),
            Expression::Index(_, _, span) => Err(CompilationError::error(
                "Array indexing type inference not yet implemented",
                *span,
                "",
            )),
            Expression::IfExpr {
                then_branch,
                else_branch,
                span,
                ..
            } => {
                let then_type = self.infer_expression(then_branch)?;
                if let Some(else_expr) = else_branch {
                    let else_type = self.infer_expression(else_expr)?;
                    if self.types_compatible(&then_type, &else_type) {
                        Ok(then_type)
                    } else {
                        Err(CompilationError::error(
                            format!(
                                "If/else branches have different types: {} vs {}",
                                then_type, else_type
                            ),
                            *span,
                            "",
                        ))
                    }
                } else {
                    Ok(then_type)
                }
            }
            Expression::WhileLoop {
                condition,
                body,
                span,
            } => {
                // Condition must be Bool
                let cond_type = self.infer_expression(condition)?;
                if cond_type != ResolvedType::Bool {
                    return Err(CompilationError::error(
                        format!("While loop condition must be bool, got {}", cond_type),
                        *span,
                        "",
                    ));
                }
                // While loop returns the type of its body
                self.infer_expression(body)
            }
            Expression::ForLoop {
                variable: _,
                range_start,
                range_end,
                body,
                span,
            } => {
                // Start and end must be integers
                let start_type = self.infer_expression(range_start)?;
                let end_type = self.infer_expression(range_end)?;

                if !matches!(start_type, ResolvedType::I32) {
                    return Err(CompilationError::error(
                        format!("For loop range start must be I32, got {}", start_type),
                        *span,
                        "",
                    ));
                }
                if !matches!(end_type, ResolvedType::I32) {
                    return Err(CompilationError::error(
                        format!("For loop range end must be I32, got {}", end_type),
                        *span,
                        "",
                    ));
                }

                // For loop returns the type of its body
                // TODO: Add loop variable to scope properly in a refactor
                self.infer_expression(body)
            }
            Expression::Break(_) => {
                // Break doesn't produce a value in our simple model
                Ok(ResolvedType::Void)
            }
            Expression::Continue(_) => {
                // Continue doesn't produce a value
                Ok(ResolvedType::Void)
            }
            Expression::Block(exprs, _span) => {
                if exprs.is_empty() {
                    Ok(ResolvedType::Void)
                } else {
                    self.infer_expression(&exprs[exprs.len() - 1])
                }
            }
        }
    }

    fn infer_literal(&self, lit: &Literal) -> Result<ResolvedType, CompilationError> {
        match lit {
            Literal::Integer(_) => Ok(ResolvedType::I32),
            Literal::Float(_) => Ok(ResolvedType::F32),
            Literal::String(_) => Ok(ResolvedType::String),
            Literal::Bool(_) => Ok(ResolvedType::Bool),
        }
    }

    fn infer_binary_op(
        &self,
        op: BinaryOp,
        left: &Expression,
        right: &Expression,
        span: Span,
    ) -> Result<ResolvedType, CompilationError> {
        let left_type = self.infer_expression(left)?;
        let right_type = self.infer_expression(right)?;

        match op {
            // Arithmetic operators return numeric types
            BinaryOp::Add | BinaryOp::Sub | BinaryOp::Mul | BinaryOp::Div | BinaryOp::Mod => {
                if !left_type.is_numeric() || !right_type.is_numeric() {
                    return Err(CompilationError::error(
                        format!(
                            "Cannot apply operator {:?} to non-numeric types {} and {}",
                            op, left_type, right_type
                        ),
                        span,
                        "",
                    ));
                }
                if self.types_compatible(&left_type, &right_type) {
                    Ok(left_type)
                } else {
                    Err(CompilationError::error(
                        format!(
                            "Type mismatch in arithmetic: {} vs {}",
                            left_type, right_type
                        ),
                        span,
                        "",
                    ))
                }
            }
            // Comparison operators return booleans
            BinaryOp::Eq
            | BinaryOp::NotEq
            | BinaryOp::Less
            | BinaryOp::LessEq
            | BinaryOp::Greater
            | BinaryOp::GreaterEq => {
                if !self.types_compatible(&left_type, &right_type) {
                    return Err(CompilationError::error(
                        format!(
                            "Cannot compare incompatible types {} and {}",
                            left_type, right_type
                        ),
                        span,
                        "",
                    ));
                }
                Ok(ResolvedType::Bool)
            }
            // Logical operators
            BinaryOp::And | BinaryOp::Or => {
                if !matches!(left_type, ResolvedType::Bool) {
                    return Err(CompilationError::error(
                        format!("Expected bool, found {}", left_type),
                        span,
                        "",
                    ));
                }
                if !matches!(right_type, ResolvedType::Bool) {
                    return Err(CompilationError::error(
                        format!("Expected bool, found {}", right_type),
                        span,
                        "",
                    ));
                }
                Ok(ResolvedType::Bool)
            }
            // Bitwise operators
            BinaryOp::BitwiseAnd | BinaryOp::BitwiseOr | BinaryOp::BitwiseXor => {
                if !left_type.is_integer() || !right_type.is_integer() {
                    return Err(CompilationError::error(
                        format!(
                            "Bitwise operators require integer types, found {} and {}",
                            left_type, right_type
                        ),
                        span,
                        "",
                    ));
                }
                if self.types_compatible(&left_type, &right_type) {
                    Ok(left_type)
                } else {
                    Err(CompilationError::error(
                        format!("Type mismatch: {} vs {}", left_type, right_type),
                        span,
                        "",
                    ))
                }
            }
            // Shift operators
            BinaryOp::LeftShift | BinaryOp::RightShift => {
                if !left_type.is_integer() {
                    return Err(CompilationError::error(
                        format!("Left operand of shift must be integer, found {}", left_type),
                        span,
                        "",
                    ));
                }
                if !right_type.is_integer() {
                    return Err(CompilationError::error(
                        format!(
                            "Right operand of shift must be integer, found {}",
                            right_type
                        ),
                        span,
                        "",
                    ));
                }
                Ok(left_type)
            }
        }
    }

    fn infer_unary_op(
        &self,
        op: UnaryOp,
        expr: &Expression,
        span: Span,
    ) -> Result<ResolvedType, CompilationError> {
        let expr_type = self.infer_expression(expr)?;

        match op {
            UnaryOp::Neg => {
                if !expr_type.is_numeric() {
                    return Err(CompilationError::error(
                        format!("Cannot negate non-numeric type {}", expr_type),
                        span,
                        "",
                    ));
                }
                Ok(expr_type)
            }
            UnaryOp::Not => {
                if !matches!(expr_type, ResolvedType::Bool) {
                    return Err(CompilationError::error(
                        format!("Cannot apply logical not to non-boolean type {}", expr_type),
                        span,
                        "",
                    ));
                }
                Ok(ResolvedType::Bool)
            }
            UnaryOp::BitwiseNot => {
                if !expr_type.is_integer() {
                    return Err(CompilationError::error(
                        format!("Bitwise not requires integer type, found {}", expr_type),
                        span,
                        "",
                    ));
                }
                Ok(expr_type)
            }
            UnaryOp::Reference => Ok(ResolvedType::Reference(Box::new(expr_type))),
            UnaryOp::MutableReference => Ok(ResolvedType::MutableReference(Box::new(expr_type))),
            UnaryOp::Dereference => {
                if let ResolvedType::Reference(inner) = expr_type {
                    Ok(*inner)
                } else if let ResolvedType::MutableReference(inner) = expr_type {
                    Ok(*inner)
                } else {
                    Err(CompilationError::error(
                        format!("Cannot dereference non-reference type {}", expr_type),
                        span,
                        "",
                    ))
                }
            }
        }
    }

    fn infer_call(
        &self,
        func_name: &str,
        args: &[Expression],
        span: Span,
    ) -> Result<ResolvedType, CompilationError> {
        let func_info = self.symbol_table.get_function(func_name).ok_or_else(|| {
            CompilationError::error(format!("Unknown function '{}'", func_name), span, "")
        })?;

        if args.len() != func_info.param_types.len() {
            return Err(CompilationError::error(
                format!(
                    "Function '{}' expects {} arguments but got {}",
                    func_name,
                    func_info.param_types.len(),
                    args.len()
                ),
                span,
                "",
            ));
        }

        // Check argument types
        for (i, (arg, expected_type)) in args.iter().zip(func_info.param_types.iter()).enumerate() {
            let arg_type = self.infer_expression(arg)?;
            if !self.types_compatible(&arg_type, expected_type) {
                return Err(CompilationError::error(
                    format!(
                        "Argument {} has type {} but expected {}",
                        i + 1,
                        arg_type,
                        expected_type
                    ),
                    span,
                    "",
                ));
            }
        }

        Ok(func_info.return_type.clone())
    }

    fn types_compatible(&self, actual: &ResolvedType, expected: &ResolvedType) -> bool {
        actual == expected
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::lexer::Lexer;
    use crate::parser::Parser;

    fn parse_program(input: &str) -> Program {
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().expect("Lexing failed");
        let parser = Parser::new(tokens);
        parser.parse_program().expect("Parsing failed")
    }

    fn check_program(program: &Program) -> Result<SymbolTable, Vec<CompilationError>> {
        let checker = TypeChecker::new();
        checker.check_program(program)
    }

    // ===== Type Resolution Tests =====

    #[test]
    fn test_resolve_primitive_types() {
        let program = parse_program("fn test() : I32 => 42;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_resolve_string_type() {
        let program = parse_program("fn test() : String => \"hello\";");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_resolve_bool_type() {
        let program = parse_program("fn test() : Bool => true;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    // ===== Function Definition Tests =====

    #[test]
    fn test_collect_function() {
        let program = parse_program("fn add(a : I32, b : I32) : I32 => a + b;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_duplicate_function_error() {
        let program = parse_program("fn add() : I32 => 1;\nfn add() : I32 => 2;");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    // ===== Struct Definition Tests =====

    #[test]
    fn test_collect_struct() {
        let program = parse_program("struct Point {\n  x : I32,\n  y : I32\n}");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_duplicate_struct_error() {
        let program = parse_program("struct Point { x : I32 }\nstruct Point { y : I32 }");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    // ===== Literal Type Inference Tests =====

    #[test]
    fn test_infer_integer_literal() {
        let program = parse_program("fn test() : I32 => 42;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_infer_float_literal() {
        let program = parse_program("fn test() : F32 => 3.14;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_infer_string_literal() {
        let program = parse_program("fn test() : String => \"hello\";");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_infer_bool_true() {
        let program = parse_program("fn test() : Bool => true;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_infer_bool_false() {
        let program = parse_program("fn test() : Bool => false;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    // ===== Arithmetic Operation Tests =====

    #[test]
    fn test_addition() {
        let program = parse_program("fn test() : I32 => 1 + 2;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_subtraction() {
        let program = parse_program("fn test() : I32 => 5 - 3;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_multiplication() {
        let program = parse_program("fn test() : I32 => 3 * 4;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_division() {
        let program = parse_program("fn test() : I32 => 8 / 2;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_modulo() {
        let program = parse_program("fn test() : I32 => 7 % 3;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    // ===== Arithmetic Type Mismatch Tests =====

    #[test]
    fn test_arithmetic_on_string_error() {
        let program = parse_program("fn test() : String => \"a\" + \"b\";");
        let result = check_program(&program);
        // String concatenation not yet supported, should error
        assert!(result.is_err());
    }

    #[test]
    fn test_arithmetic_on_bool_error() {
        let program = parse_program("fn test() : Bool => true + false;");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    // ===== Comparison Operation Tests =====

    #[test]
    fn test_equality() {
        let program = parse_program("fn test() : Bool => 5 == 5;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_inequality() {
        let program = parse_program("fn test() : Bool => 5 != 3;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_less_than() {
        let program = parse_program("fn test() : Bool => 3 < 5;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_less_equal() {
        let program = parse_program("fn test() : Bool => 5 <= 5;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_greater_than() {
        let program = parse_program("fn test() : Bool => 5 > 3;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_greater_equal() {
        let program = parse_program("fn test() : Bool => 5 >= 5;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    // ===== Logical Operation Tests =====

    #[test]
    fn test_logical_and() {
        let program = parse_program("fn test() : Bool => true && false;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_logical_or() {
        let program = parse_program("fn test() : Bool => true || false;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_logical_and_non_bool_error() {
        let program = parse_program("fn test() : Bool => 5 && 3;");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    // ===== Bitwise Operation Tests =====

    #[test]
    fn test_bitwise_and() {
        let program = parse_program("fn test() : I32 => 5 & 3;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_bitwise_or() {
        let program = parse_program("fn test() : I32 => 5 | 3;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_bitwise_xor() {
        let program = parse_program("fn test() : I32 => 5 ^ 3;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_left_shift() {
        let program = parse_program("fn test() : I32 => 5 << 2;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_right_shift() {
        let program = parse_program("fn test() : I32 => 5 >> 2;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    // ===== Unary Operation Tests =====

    #[test]
    fn test_unary_negation() {
        let program = parse_program("fn test() : I32 => -42;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_unary_not() {
        let program = parse_program("fn test() : Bool => !true;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_unary_not_on_int_error() {
        let program = parse_program("fn test() : Bool => !42;");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    // ===== Function Call Tests =====

    #[test]
    fn test_function_call() {
        let program = parse_program(
            "fn add(a : I32, b : I32) : I32 => a + b;\nfn test() : I32 => add(1, 2);",
        );
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_unknown_function_error() {
        let program = parse_program("fn test() : I32 => unknown();");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    #[test]
    fn test_wrong_arg_count_error() {
        let program =
            parse_program("fn add(a : I32, b : I32) : I32 => a + b;\nfn test() : I32 => add(1);");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    #[test]
    fn test_wrong_arg_type_error() {
        let program = parse_program(
            "fn add(a : I32, b : I32) : I32 => a + b;\nfn test() : I32 => add(true, false);",
        );
        let result = check_program(&program);
        assert!(result.is_err());
    }

    // ===== Return Type Checking Tests =====

    #[test]
    fn test_correct_return_type() {
        let program = parse_program("fn test() : I32 => 42;");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_wrong_return_type_error() {
        let program = parse_program("fn test() : I32 => true;");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    #[test]
    fn test_string_return() {
        let program = parse_program("fn test() : String => \"hello\";");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    // ===== If Expression Tests =====

    #[test]
    fn test_if_expression_same_types() {
        let program = parse_program("fn test() : I32 => if true { 1 } else { 2 };");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_if_expression_different_types_error() {
        let program = parse_program("fn test() : I32 => if true { 1 } else { \"two\" };");
        let result = check_program(&program);
        assert!(result.is_err());
    }

    // ===== Complex Expression Tests =====

    #[test]
    fn test_nested_arithmetic() {
        let program = parse_program("fn test() : I32 => (1 + 2) * (3 + 4);");
        let result = check_program(&program);
        assert!(result.is_ok());
    }

    #[test]
    fn test_complex_logical() {
        let program = parse_program("fn test() : Bool => (5 > 3) && (2 < 4);");
        let result = check_program(&program);
        assert!(result.is_ok());
    }
}
