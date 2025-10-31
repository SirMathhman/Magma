/// Lower semantic analysis output (AST + types) to HIR
///
/// Converts fully-typed AST from semantic analysis into ownership-annotated HIR
/// ready for code generation. Handles:
/// - Type-to-ownership mapping (e.g., references become Borrowed)
/// - Expression desugaring (complex -> simple operations)
/// - Span preservation for error reporting
use crate::ast::*;
use crate::diagnostics::CompilationError;
use crate::hir::*;
use crate::semantic::{ResolvedType, SymbolTable};
use crate::token::Span;

/// Lower a program from AST to HIR
pub struct HirLowering {
    symbol_table: SymbolTable,
}

impl HirLowering {
    pub fn new(symbol_table: SymbolTable) -> Self {
        HirLowering { symbol_table }
    }

    /// Lower a complete program to HIR
    pub fn lower_program(&self, program: &Program) -> Result<HirProgram, Vec<CompilationError>> {
        let mut items = Vec::new();
        let mut errors = Vec::new();

        for item in &program.items {
            match item {
                Item::FunctionDef(func) => match self.lower_function(func) {
                    Ok(hir_func) => items.push(HirItem::Function(hir_func)),
                    Err(e) => errors.push(e),
                },
                Item::StructDef(struct_def) => match self.lower_struct(struct_def) {
                    Ok(hir_struct) => items.push(HirItem::Struct(hir_struct)),
                    Err(e) => errors.push(e),
                },
                Item::ClassDef(class_def) => match self.lower_class(class_def) {
                    Ok(hir_class) => items.push(HirItem::Class(hir_class)),
                    Err(e) => errors.push(e),
                },
            }
        }

        if !errors.is_empty() {
            return Err(errors);
        }

        Ok(HirProgram { items })
    }

    /// Lower a function definition to HIR
    fn lower_function(&self, func: &FunctionDef) -> Result<HirFunction, CompilationError> {
        let params = func
            .params
            .iter()
            .map(|p| self.lower_param(p))
            .collect::<Result<Vec<_>, _>>()?;

        let return_type = self.resolve_type(&func.return_type)?;
        let body = self.lower_expr(&func.body, &return_type)?;

        Ok(HirFunction {
            name: func.name.clone(),
            params,
            return_type,
            body,
            span: func.span,
        })
    }

    /// Lower a struct definition to HIR
    fn lower_struct(&self, struct_def: &StructDef) -> Result<HirStruct, CompilationError> {
        let fields = struct_def
            .fields
            .iter()
            .map(|f| {
                let ty = self.resolve_type(&f.ty)?;
                Ok(HirField {
                    name: f.name.clone(),
                    ty,
                    span: f.span,
                })
            })
            .collect::<Result<Vec<_>, _>>()?;

        Ok(HirStruct {
            name: struct_def.name.clone(),
            fields,
            span: struct_def.span,
        })
    }

    /// Lower a class definition to HIR
    fn lower_class(&self, class_def: &ClassDef) -> Result<HirClass, CompilationError> {
        let constructor = self.lower_function(&class_def.constructor)?;

        Ok(HirClass {
            name: class_def.constructor.name.clone(),
            constructor,
            span: class_def.span,
        })
    }

    /// Lower a function parameter
    fn lower_param(&self, param: &Parameter) -> Result<HirParam, CompilationError> {
        let ty = self.resolve_type(&param.ty)?;
        let ownership = self.ownership_of_type(&ty);

        Ok(HirParam {
            name: param.name.clone(),
            ty,
            ownership,
            span: param.span,
        })
    }

    /// Determine ownership of a type
    fn ownership_of_type(&self, ty: &ResolvedType) -> Ownership {
        match ty {
            ResolvedType::Reference(_) => Ownership::Borrowed,
            ResolvedType::MutableReference(_) => Ownership::MutableBorrowed,
            _ if ty.is_copy() => Ownership::Copy,
            _ => Ownership::Owned,
        }
    }

    /// Lower an expression to HIR
    fn lower_expr(
        &self,
        expr: &Expression,
        expected_type: &ResolvedType,
    ) -> Result<HirExpr, CompilationError> {
        let (kind, ty) = match expr {
            Expression::Literal(lit, span) => {
                let hir_lit = self.lower_literal(lit);
                let ty = self.literal_type(lit);
                (HirExprKind::Literal(hir_lit), ty)
            }
            Expression::Identifier(name, _span) => {
                // For now, assume identifier type inference was done by semantic analyzer
                (HirExprKind::Var(name.clone()), expected_type.clone())
            }
            Expression::Binary(op, left, right, span) => {
                let hir_op = self.lower_binary_op(*op);
                let left_hir = self.lower_expr(left, expected_type)?;
                let right_hir = self.lower_expr(right, expected_type)?;
                let ty = left_hir.ty.clone();

                (
                    HirExprKind::Binary {
                        op: hir_op,
                        left: Box::new(left_hir),
                        right: Box::new(right_hir),
                    },
                    ty,
                )
            }
            Expression::Unary(op, inner, span) => {
                let hir_op = self.lower_unary_op(*op);
                let inner_hir = self.lower_expr(inner, expected_type)?;
                let ty = match hir_op {
                    HirUnaryOp::Reference => {
                        ResolvedType::Reference(Box::new(inner_hir.ty.clone()))
                    }
                    HirUnaryOp::MutableReference => {
                        ResolvedType::MutableReference(Box::new(inner_hir.ty.clone()))
                    }
                    HirUnaryOp::Deref => match &inner_hir.ty {
                        ResolvedType::Reference(t) => (**t).clone(),
                        ResolvedType::MutableReference(t) => (**t).clone(),
                        _ => expected_type.clone(),
                    },
                    _ => inner_hir.ty.clone(),
                };

                (
                    HirExprKind::Unary {
                        op: hir_op,
                        expr: Box::new(inner_hir),
                    },
                    ty,
                )
            }
            Expression::Call(func_expr, args, span) => {
                if let Expression::Identifier(func_name, _) = &**func_expr {
                    // Special handling for let bindings
                    if func_name == "_let_binding" {
                        // _let_binding is a pseudo-function used by the parser for let statements
                        // It takes 2 args: (variable_name, value_expr)
                        // Just lower the value expression and return it
                        if args.len() == 2 {
                            return self.lower_expr(&args[1], expected_type);
                        } else {
                            return Err(CompilationError::error(
                                "Invalid let binding construct".to_string(),
                                *span,
                                "",
                            ));
                        }
                    }

                    let func_info = self.symbol_table.get_function(func_name).ok_or_else(|| {
                        CompilationError::error(
                            format!("Unknown function '{}'", func_name),
                            *span,
                            "",
                        )
                    })?;

                    let hir_args = args
                        .iter()
                        .zip(func_info.param_types.iter())
                        .map(|(arg, param_ty)| self.lower_expr(arg, param_ty))
                        .collect::<Result<Vec<_>, _>>()?;

                    (
                        HirExprKind::Call {
                            func: func_name.clone(),
                            args: hir_args,
                        },
                        func_info.return_type.clone(),
                    )
                } else {
                    return Err(CompilationError::error(
                        "Complex function calls not yet supported in HIR",
                        *span,
                        "",
                    ));
                }
            }
            Expression::FieldAccess(expr, field, span) => {
                let inner_hir = self.lower_expr(expr, expected_type)?;
                (
                    HirExprKind::FieldAccess {
                        expr: Box::new(inner_hir),
                        field: field.clone(),
                    },
                    expected_type.clone(),
                )
            }
            Expression::Index(expr, index, span) => {
                let expr_hir = self.lower_expr(expr, expected_type)?;
                let index_hir = self.lower_expr(index, &ResolvedType::I32)?;
                (
                    HirExprKind::Index {
                        expr: Box::new(expr_hir),
                        index: Box::new(index_hir),
                    },
                    expected_type.clone(),
                )
            }
            Expression::IfExpr {
                condition,
                then_branch,
                else_branch,
                span,
            } => {
                let cond_hir = self.lower_expr(condition, &ResolvedType::Bool)?;
                let then_hir = self.lower_expr(then_branch, expected_type)?;
                let else_hir = if let Some(else_expr) = else_branch {
                    Some(Box::new(self.lower_expr(else_expr, expected_type)?))
                } else {
                    None
                };

                let ty = then_hir.ty.clone();

                (
                    HirExprKind::If {
                        cond: Box::new(cond_hir),
                        then_expr: Box::new(then_hir),
                        else_expr: else_hir,
                    },
                    ty,
                )
            }
            Expression::WhileLoop {
                condition,
                body,
                span,
            } => {
                let cond_hir = self.lower_expr(condition, &ResolvedType::Bool)?;
                let body_hir = self.lower_expr(body, expected_type)?;
                let ty = body_hir.ty.clone();

                (
                    HirExprKind::While {
                        cond: Box::new(cond_hir),
                        body: Box::new(body_hir),
                    },
                    ty,
                )
            }
            Expression::ForLoop {
                variable,
                range_start,
                range_end,
                body,
                span,
            } => {
                let start_hir = self.lower_expr(range_start, &ResolvedType::I32)?;
                let end_hir = self.lower_expr(range_end, &ResolvedType::I32)?;
                let body_hir = self.lower_expr(body, expected_type)?;
                let ty = body_hir.ty.clone();

                (
                    HirExprKind::For {
                        var: variable.clone(),
                        start: Box::new(start_hir),
                        end: Box::new(end_hir),
                        body: Box::new(body_hir),
                    },
                    ty,
                )
            }
            Expression::Break(_) => (HirExprKind::Break, ResolvedType::Void),
            Expression::Continue(_) => (HirExprKind::Continue, ResolvedType::Void),
            Expression::Block(exprs, _span) => {
                if exprs.is_empty() {
                    (HirExprKind::Block(Vec::new()), ResolvedType::Void)
                } else {
                    let hir_exprs = exprs
                        .iter()
                        .enumerate()
                        .map(|(i, e)| {
                            let ty = if i == exprs.len() - 1 {
                                expected_type
                            } else {
                                &ResolvedType::Void
                            };
                            self.lower_expr(e, ty)
                        })
                        .collect::<Result<Vec<_>, _>>()?;

                    let block_type = hir_exprs
                        .last()
                        .map(|e| e.ty.clone())
                        .unwrap_or(ResolvedType::Void);

                    (HirExprKind::Block(hir_exprs), block_type)
                }
            }
        };

        let ownership = self.ownership_of_type(&ty);
        let span = expr.span();

        Ok(HirExpr {
            kind,
            ty,
            ownership,
            span,
        })
    }

    fn lower_literal(&self, lit: &Literal) -> HirLiteral {
        match lit {
            Literal::Integer(n) => HirLiteral::Integer(*n),
            Literal::Float(f) => HirLiteral::Float(*f),
            Literal::String(s) => HirLiteral::String(s.clone()),
            Literal::Bool(b) => HirLiteral::Bool(*b),
        }
    }

    fn literal_type(&self, lit: &Literal) -> ResolvedType {
        match lit {
            Literal::Integer(_) => ResolvedType::I32,
            Literal::Float(_) => ResolvedType::F32,
            Literal::String(_) => ResolvedType::String,
            Literal::Bool(_) => ResolvedType::Bool,
        }
    }

    fn lower_binary_op(&self, op: BinaryOp) -> HirBinaryOp {
        match op {
            BinaryOp::Add => HirBinaryOp::Add,
            BinaryOp::Sub => HirBinaryOp::Sub,
            BinaryOp::Mul => HirBinaryOp::Mul,
            BinaryOp::Div => HirBinaryOp::Div,
            BinaryOp::Mod => HirBinaryOp::Mod,
            BinaryOp::Eq => HirBinaryOp::Eq,
            BinaryOp::NotEq => HirBinaryOp::NotEq,
            BinaryOp::Less => HirBinaryOp::Less,
            BinaryOp::LessEq => HirBinaryOp::LessEq,
            BinaryOp::Greater => HirBinaryOp::Greater,
            BinaryOp::GreaterEq => HirBinaryOp::GreaterEq,
            BinaryOp::And => HirBinaryOp::And,
            BinaryOp::Or => HirBinaryOp::Or,
            BinaryOp::BitwiseAnd => HirBinaryOp::BitwiseAnd,
            BinaryOp::BitwiseOr => HirBinaryOp::BitwiseOr,
            BinaryOp::BitwiseXor => HirBinaryOp::BitwiseXor,
            BinaryOp::LeftShift => HirBinaryOp::LeftShift,
            BinaryOp::RightShift => HirBinaryOp::RightShift,
        }
    }

    fn lower_unary_op(&self, op: UnaryOp) -> HirUnaryOp {
        match op {
            UnaryOp::Neg => HirUnaryOp::Neg,
            UnaryOp::Not => HirUnaryOp::Not,
            UnaryOp::BitwiseNot => HirUnaryOp::BitwiseNot,
            UnaryOp::Reference => HirUnaryOp::Reference,
            UnaryOp::MutableReference => HirUnaryOp::MutableReference,
            UnaryOp::Dereference => HirUnaryOp::Deref,
        }
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
                _ => Ok(ResolvedType::Struct(name.clone())),
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
                resolved?.first().cloned().ok_or_else(|| {
                    CompilationError::error("Empty tuple type", Span::new(0, 0, 0, 0), "")
                })
            }
            Type::Array(inner) => Ok(ResolvedType::Array(Box::new(self.resolve_type(inner)?))),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::lexer::Lexer;
    use crate::parser::Parser;
    use crate::semantic::TypeChecker;

    fn parse_and_lower(input: &str) -> Result<HirProgram, Vec<CompilationError>> {
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize().expect("Lexing failed");
        let parser = Parser::new(tokens);
        let program = parser.parse_program().expect("Parsing failed");

        let checker = TypeChecker::new();
        let symbol_table = checker.check_program(&program)?;

        let lowering = HirLowering::new(symbol_table);
        lowering.lower_program(&program)
    }

    #[test]
    fn test_lower_simple_function() {
        let result = parse_and_lower("fn test() : I32 => 42;");
        assert!(result.is_ok());
        let hir = result.unwrap();
        assert_eq!(hir.items.len(), 1);
    }

    #[test]
    fn test_lower_function_with_params() {
        let result = parse_and_lower("fn add(a : I32, b : I32) : I32 => a + b;");
        assert!(result.is_ok());
        let hir = result.unwrap();
        assert_eq!(hir.items.len(), 1);
    }

    #[test]
    fn test_lower_arithmetic_expression() {
        let result = parse_and_lower("fn test() : I32 => 1 + 2 * 3;");
        assert!(result.is_ok());
    }

    #[test]
    fn test_lower_if_expression() {
        let result = parse_and_lower("fn test() : I32 => if true { 1 } else { 2 };");
        assert!(result.is_ok());
    }

    #[test]
    fn test_lower_struct() {
        let result = parse_and_lower("struct Point { x : I32, y : I32 }");
        assert!(result.is_ok());
        let hir = result.unwrap();
        assert_eq!(hir.items.len(), 1);
    }

    #[test]
    fn test_lower_block_expression() {
        let result = parse_and_lower("fn test() : I32 => { 1; 2; 3 };");
        assert!(result.is_ok());
    }

    #[test]
    fn test_ownership_of_primitives() {
        let lowering = HirLowering::new(SymbolTable::new());
        assert_eq!(
            lowering.ownership_of_type(&ResolvedType::I32),
            Ownership::Copy
        );
        assert_eq!(
            lowering.ownership_of_type(&ResolvedType::Bool),
            Ownership::Copy
        );
    }

    #[test]
    fn test_ownership_of_owned_type() {
        let lowering = HirLowering::new(SymbolTable::new());
        assert_eq!(
            lowering.ownership_of_type(&ResolvedType::String),
            Ownership::Owned
        );
    }

    #[test]
    fn test_ownership_of_reference() {
        let lowering = HirLowering::new(SymbolTable::new());
        let ref_type = ResolvedType::Reference(Box::new(ResolvedType::I32));
        assert_eq!(lowering.ownership_of_type(&ref_type), Ownership::Borrowed);
    }

    #[test]
    fn test_ownership_of_mut_reference() {
        let lowering = HirLowering::new(SymbolTable::new());
        let ref_type = ResolvedType::MutableReference(Box::new(ResolvedType::I32));
        assert_eq!(
            lowering.ownership_of_type(&ref_type),
            Ownership::MutableBorrowed
        );
    }

    #[test]
    fn test_lower_function_call() {
        let result = parse_and_lower(
            "fn add(a : I32, b : I32) : I32 => a + b;\nfn test() : I32 => add(1, 2);",
        );
        assert!(result.is_ok());
    }

    #[test]
    fn test_lower_comparison_expression() {
        let result = parse_and_lower("fn test() : Bool => 5 > 3;");
        assert!(result.is_ok());
    }

    #[test]
    fn test_lower_logical_expression() {
        let result = parse_and_lower("fn test() : Bool => true && false;");
        assert!(result.is_ok());
    }

    #[test]
    fn test_lower_nested_expressions() {
        let result = parse_and_lower("fn test() : I32 => (1 + 2) * (3 + 4);");
        assert!(result.is_ok());
    }
}
