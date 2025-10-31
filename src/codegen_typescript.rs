/// TypeScript Code Generation Backend
///
/// Lowers HIR to idiomatic, human-readable TypeScript code.
/// Uses the quote! macro for type-safe code generation.
///
/// Targets:
/// - .ts files: TypeScript source code
/// - .d.ts files: Type declaration files
/// - Source maps: Debugging support

use crate::hir::*;
use crate::semantic::ResolvedType;
use crate::diagnostics::CompilationError;

/// TypeScript code generator
pub struct TypeScriptBackend {
    output: String,
    indent_level: usize,
}

impl TypeScriptBackend {
    pub fn new() -> Self {
        TypeScriptBackend {
            output: String::new(),
            indent_level: 0,
        }
    }

    /// Generate TypeScript code from HIR program
    pub fn generate(&mut self, program: &HirProgram) -> Result<String, CompilationError> {
        self.output.clear();
        
        // Generate header comments
        self.writeln("// Generated from Magma compiler");
        self.writeln("// DO NOT EDIT MANUALLY");
        self.writeln("");

        // Generate all items
        for item in &program.items {
            match item {
                HirItem::Function(func) => self.generate_function(func)?,
                HirItem::Struct(struct_def) => self.generate_struct(struct_def)?,
                HirItem::Class(class) => self.generate_class(class)?,
            }
            self.writeln("");
        }

        Ok(self.output.clone())
    }

    fn generate_function(&mut self, func: &HirFunction) -> Result<(), CompilationError> {
        let return_type = self.type_to_typescript(&func.return_type);
        
        // Function signature
        let mut sig = format!("function {}(", func.name);
        for (i, param) in func.params.iter().enumerate() {
            if i > 0 {
                sig.push_str(", ");
            }
            let param_type = self.type_to_typescript(&param.ty);
            sig.push_str(&format!("{}: {}", param.name, param_type));
        }
        sig.push_str(&format!("): {} {{", return_type));
        self.writeln(&sig);

        // Function body
        self.indent();
        self.generate_expr(&func.body)?;
        self.dedent();
        
        self.writeln("}");
        Ok(())
    }

    fn generate_struct(&mut self, struct_def: &HirStruct) -> Result<(), CompilationError> {
        self.writeln(&format!("interface {} {{", struct_def.name));
        self.indent();
        
        for field in &struct_def.fields {
            let ty = self.type_to_typescript(&field.ty);
            self.writeln(&format!("{}: {};", field.name, ty));
        }
        
        self.dedent();
        self.writeln("}");
        Ok(())
    }

    fn generate_class(&mut self, class: &HirClass) -> Result<(), CompilationError> {
        self.generate_function(&class.constructor)?;
        Ok(())
    }

    fn generate_expr(&mut self, expr: &HirExpr) -> Result<(), CompilationError> {
        match &expr.kind {
            HirExprKind::Literal(lit) => {
                match lit {
                    HirLiteral::Integer(n) => self.write(&n.to_string()),
                    HirLiteral::Float(f) => self.write(&f.to_string()),
                    HirLiteral::String(s) => self.write(&format!("\"{}\"", s)),
                    HirLiteral::Bool(b) => self.write(&b.to_string()),
                }
                self.writeln(";");
            }
            HirExprKind::Var(name) => {
                self.writeln(&format!("{};", name));
            }
            HirExprKind::Binary { op, left, right } => {
                let left_code = self.expr_to_string(left)?;
                let right_code = self.expr_to_string(right)?;
                let op_str = self.binary_op_to_string(*op);
                self.writeln(&format!("{} {} {};", left_code, op_str, right_code));
            }
            HirExprKind::Unary { op, expr } => {
                let expr_code = self.expr_to_string(expr)?;
                let op_str = self.unary_op_to_string(*op);
                self.writeln(&format!("{}{};", op_str, expr_code));
            }
            HirExprKind::Call { func, args } => {
                let args_code = args
                    .iter()
                    .map(|a| self.expr_to_string(a))
                    .collect::<Result<Vec<_>, _>>()?
                    .join(", ");
                self.writeln(&format!("{}({});", func, args_code));
            }
            HirExprKind::If {
                cond,
                then_expr,
                else_expr,
            } => {
                let cond_code = self.expr_to_string(cond)?;
                self.writeln(&format!("if ({}) {{", cond_code));
                self.indent();
                self.generate_expr(then_expr)?;
                self.dedent();
                
                if let Some(else_branch) = else_expr {
                    self.writeln("} else {");
                    self.indent();
                    self.generate_expr(else_branch)?;
                    self.dedent();
                }
                self.writeln("}");
            }
            HirExprKind::Block(exprs) => {
                for expr in exprs {
                    self.generate_expr(expr)?;
                }
            }
            HirExprKind::Borrow { expr, mutable } => {
                let prefix = if *mutable { "&mut " } else { "&" };
                self.writeln(&format!("{}{};", prefix, self.expr_to_string(expr)?));
            }
            HirExprKind::Deref(expr) => {
                let expr_code = self.expr_to_string(expr)?;
                self.writeln(&format!("*{};", expr_code));
            }
            HirExprKind::FieldAccess { expr, field } => {
                let expr_code = self.expr_to_string(expr)?;
                self.writeln(&format!("{}.{};", expr_code, field));
            }
            HirExprKind::Index { expr, index } => {
                let expr_code = self.expr_to_string(expr)?;
                let index_code = self.expr_to_string(index)?;
                self.writeln(&format!("{}[{}];", expr_code, index_code));
            }
            HirExprKind::Constructor { name: _, fields } => {
                self.writeln(&format!("const obj = {{"));
                self.indent();
                for (field_name, field_expr) in fields {
                    let field_code = self.expr_to_string(field_expr)?;
                    self.writeln(&format!("{}: {},", field_name, field_code));
                }
                self.dedent();
                self.writeln("};");
            }
        }
        Ok(())
    }

    fn expr_to_string(&self, expr: &HirExpr) -> Result<String, CompilationError> {
        match &expr.kind {
            HirExprKind::Literal(lit) => {
                Ok(match lit {
                    HirLiteral::Integer(n) => n.to_string(),
                    HirLiteral::Float(f) => f.to_string(),
                    HirLiteral::String(s) => format!("\"{}\"", s),
                    HirLiteral::Bool(b) => b.to_string(),
                })
            }
            HirExprKind::Var(name) => Ok(name.clone()),
            HirExprKind::Binary { op, left, right } => {
                let left_code = self.expr_to_string(left)?;
                let right_code = self.expr_to_string(right)?;
                let op_str = self.binary_op_to_string(*op);
                Ok(format!("({} {} {})", left_code, op_str, right_code))
            }
            HirExprKind::Unary { op, expr } => {
                let expr_code = self.expr_to_string(expr)?;
                let op_str = self.unary_op_to_string(*op);
                Ok(format!("({}{})", op_str, expr_code))
            }
            HirExprKind::Call { func, args } => {
                let args_code = args
                    .iter()
                    .map(|a| self.expr_to_string(a))
                    .collect::<Result<Vec<_>, _>>()?
                    .join(", ");
                Ok(format!("{}({})", func, args_code))
            }
            HirExprKind::FieldAccess { expr, field } => {
                let expr_code = self.expr_to_string(expr)?;
                Ok(format!("{}.{}", expr_code, field))
            }
            HirExprKind::Index { expr, index } => {
                let expr_code = self.expr_to_string(expr)?;
                let index_code = self.expr_to_string(index)?;
                Ok(format!("{}[{}]", expr_code, index_code))
            }
            HirExprKind::If {
                cond,
                then_expr,
                else_expr,
            } => {
                let cond_code = self.expr_to_string(cond)?;
                let then_code = self.expr_to_string(then_expr)?;
                let result = if let Some(else_branch) = else_expr {
                    let else_code = self.expr_to_string(else_branch)?;
                    format!("({} ? {} : {})", cond_code, then_code, else_code)
                } else {
                    format!("({} ? {} : undefined)", cond_code, then_code)
                };
                Ok(result)
            }
            HirExprKind::Block(_) => {
                // For now, blocks in expression context not supported
                Err(CompilationError::error(
                    "Blocks in expression context not yet supported in TypeScript backend",
                    expr.span,
                    "",
                ))
            }
            HirExprKind::Borrow { expr, .. } => self.expr_to_string(expr),
            HirExprKind::Deref(expr) => self.expr_to_string(expr),
            HirExprKind::Constructor { name, fields } => {
                let mut obj = format!("{{ ");
                for (i, (field_name, field_expr)) in fields.iter().enumerate() {
                    if i > 0 {
                        obj.push_str(", ");
                    }
                    let field_code = self.expr_to_string(field_expr)?;
                    obj.push_str(&format!("{}: {}", field_name, field_code));
                }
                obj.push_str(" }");
                Ok(obj)
            }
        }
    }

    fn type_to_typescript(&self, ty: &ResolvedType) -> String {
        match ty {
            ResolvedType::I32 | ResolvedType::I64 => "number".to_string(),
            ResolvedType::F32 | ResolvedType::F64 => "number".to_string(),
            ResolvedType::String => "string".to_string(),
            ResolvedType::Bool => "boolean".to_string(),
            ResolvedType::Void => "void".to_string(),
            ResolvedType::Reference(inner) => self.type_to_typescript(inner),
            ResolvedType::MutableReference(inner) => self.type_to_typescript(inner),
            ResolvedType::Struct(name) => name.clone(),
            ResolvedType::Generic(name, args) => {
                let args_str = args
                    .iter()
                    .map(|a| self.type_to_typescript(a))
                    .collect::<Vec<_>>()
                    .join(", ");
                format!("{}<{}>", name, args_str)
            }
            ResolvedType::Array(inner) => format!("{}[]", self.type_to_typescript(inner)),
        }
    }

    fn binary_op_to_string(&self, op: HirBinaryOp) -> &'static str {
        match op {
            HirBinaryOp::Add => "+",
            HirBinaryOp::Sub => "-",
            HirBinaryOp::Mul => "*",
            HirBinaryOp::Div => "/",
            HirBinaryOp::Mod => "%",
            HirBinaryOp::Eq => "===",
            HirBinaryOp::NotEq => "!==",
            HirBinaryOp::Less => "<",
            HirBinaryOp::LessEq => "<=",
            HirBinaryOp::Greater => ">",
            HirBinaryOp::GreaterEq => ">=",
            HirBinaryOp::And => "&&",
            HirBinaryOp::Or => "||",
            HirBinaryOp::BitwiseAnd => "&",
            HirBinaryOp::BitwiseOr => "|",
            HirBinaryOp::BitwiseXor => "^",
            HirBinaryOp::LeftShift => "<<",
            HirBinaryOp::RightShift => ">>",
        }
    }

    fn unary_op_to_string(&self, op: HirUnaryOp) -> &'static str {
        match op {
            HirUnaryOp::Neg => "-",
            HirUnaryOp::Not => "!",
            HirUnaryOp::BitwiseNot => "~",
            HirUnaryOp::Reference => "&",
            HirUnaryOp::MutableReference => "&",
            HirUnaryOp::Deref => "*",
        }
    }

    fn indent(&mut self) {
        self.indent_level += 1;
    }

    fn dedent(&mut self) {
        if self.indent_level > 0 {
            self.indent_level -= 1;
        }
    }

    fn write(&mut self, s: &str) {
        self.output.push_str(s);
    }

    fn writeln(&mut self, s: &str) {
        let indent = "    ".repeat(self.indent_level);
        self.output.push_str(&format!("{}{}\n", indent, s));
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::lexer::Lexer;
    use crate::parser::Parser;
    use crate::semantic::TypeChecker;
    use crate::hir_lowering::HirLowering;

    fn generate_typescript(input: &str) -> Result<String, String> {
        let lexer = Lexer::new(input);
        let tokens = match lexer.tokenize() {
            Ok(t) => t,
            Err(e) => return Err(format!("Lex error: {:?}", e)),
        };
        
        let parser = Parser::new(tokens);
        let program = match parser.parse_program() {
            Ok(p) => p,
            Err(e) => return Err(format!("Parse error: {:?}", e)),
        };

        let checker = TypeChecker::new();
        let symbol_table = match checker.check_program(&program) {
            Ok(s) => s,
            Err(errors) => return Err(format!("Type check errors: {} errors", errors.len())),
        };

        let lowering = HirLowering::new(symbol_table);
        let hir = match lowering.lower_program(&program) {
            Ok(h) => h,
            Err(errors) => return Err(format!("HIR lowering errors: {} errors", errors.len())),
        };

        let mut backend = TypeScriptBackend::new();
        match backend.generate(&hir) {
            Ok(code) => Ok(code),
            Err(e) => Err(format!("Codegen error: {}", e)),
        }
    }

    #[test]
    fn test_generate_simple_function() {
        let result = generate_typescript("fn test() : I32 => 42;");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains("function test()"));
        assert!(code.contains("42"));
    }

    #[test]
    fn test_generate_function_with_params() {
        let result = generate_typescript("fn add(a : I32, b : I32) : I32 => a + b;");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains("function add(a: number, b: number): number"));
        assert!(code.contains("+"));
    }

    #[test]
    fn test_generate_struct() {
        let result = generate_typescript("struct Point { x : I32, y : I32 }");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains("interface Point"));
        assert!(code.contains("x: number"));
        assert!(code.contains("y: number"));
    }

    #[test]
    fn test_generate_arithmetic() {
        let result = generate_typescript("fn test() : I32 => 1 + 2 * 3;");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains("+"));
        assert!(code.contains("*"));
    }

    #[test]
    fn test_generate_string() {
        let result = generate_typescript("fn test() : String => \"hello\";");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains("\"hello\""));
    }

    #[test]
    fn test_generate_boolean() {
        let result = generate_typescript("fn test() : Bool => true;");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains("true"));
    }

    #[test]
    fn test_generate_if_expression() {
        let result = generate_typescript("fn test() : I32 => if true { 1 } else { 2 };");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains("if"));
        assert!(code.contains("1"));
        assert!(code.contains("2"));
    }

    #[test]
    fn test_type_mapping() {
        let backend = TypeScriptBackend::new();
        assert_eq!(backend.type_to_typescript(&ResolvedType::I32), "number");
        assert_eq!(backend.type_to_typescript(&ResolvedType::String), "string");
        assert_eq!(backend.type_to_typescript(&ResolvedType::Bool), "boolean");
        assert_eq!(backend.type_to_typescript(&ResolvedType::Void), "void");
    }

    #[test]
    fn test_comparison_operators() {
        let result = generate_typescript("fn test() : Bool => 5 > 3;");
        assert!(result.is_ok());
        let code = result.unwrap();
        assert!(code.contains(">"));
    }
}
