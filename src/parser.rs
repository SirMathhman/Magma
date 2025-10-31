/// Parser for the Magma language
///
/// Converts a token stream into an Abstract Syntax Tree (AST).
/// Uses recursive descent parsing with operator precedence climbing.
/// Week 2 Implementation

use crate::token::{Token, TokenKind, Span};
use crate::ast::*;
use crate::diagnostics::CompilationError;

pub struct Parser {
    tokens: Vec<Token>,
    position: usize,
}

impl Parser {
    pub fn new(tokens: Vec<Token>) -> Self {
        Parser {
            tokens,
            position: 0,
        }
    }

    /// Parse a complete program
    pub fn parse_program(mut self) -> Result<Program, Vec<CompilationError>> {
        let mut items = Vec::new();
        let mut errors = Vec::new();

        while !self.is_at_end() {
            match self.parse_item() {
                Ok(item) => items.push(item),
                Err(e) => {
                    errors.push(e);
                    self.synchronize();
                }
            }
        }

        if !errors.is_empty() {
            return Err(errors);
        }

        let span = if items.is_empty() {
            self.current_span()
        } else {
            Span::merge(items[0].span(), items[items.len() - 1].span())
        };

        Ok(Program { items, span })
    }

    // ===== Item Parsing =====

    fn parse_item(&mut self) -> Result<Item, CompilationError> {
        match &self.peek().kind {
            TokenKind::Fn => {
                let func = self.parse_function()?;
                Ok(Item::FunctionDef(func))
            }
            TokenKind::Struct => {
                let struct_def = self.parse_struct()?;
                Ok(Item::StructDef(struct_def))
            }
            TokenKind::Class => {
                let class_def = self.parse_class()?;
                Ok(Item::ClassDef(class_def))
            }
            _ => Err(CompilationError::error(
                "Expected 'fn', 'struct', or 'class'",
                self.current_span(),
                "",
            )),
        }
    }

    fn parse_function(&mut self) -> Result<FunctionDef, CompilationError> {
        let start_span = self.consume_kind(TokenKind::Fn)?;
        let name = self.consume_identifier()?;
        self.consume_kind(TokenKind::LeftParen)?;

        let mut params = Vec::new();
        if self.peek().kind != TokenKind::RightParen {
            params.push(self.parse_parameter()?);
            while self.peek().kind == TokenKind::Comma {
                self.advance();
                params.push(self.parse_parameter()?);
            }
        }
        self.consume_kind(TokenKind::RightParen)?;

        self.consume_kind(TokenKind::Colon)?;
        let return_type = self.parse_type()?;

        self.consume_kind(TokenKind::Arrow)?;

        let body = self.parse_expression()?;

        self.consume_kind(TokenKind::Semicolon)?;

        let span = Span::merge(start_span, body.span());

        Ok(FunctionDef {
            name,
            params,
            return_type,
            body,
            span,
        })
    }

    fn parse_parameter(&mut self) -> Result<Parameter, CompilationError> {
        let start_span = self.current_span();
        let name = self.consume_identifier()?;
        self.consume_kind(TokenKind::Colon)?;
        let ty = self.parse_type()?;

        let span = Span::merge(start_span, ty_span(&ty));

        Ok(Parameter {
            name,
            ty,
            span,
        })
    }

    fn parse_struct(&mut self) -> Result<StructDef, CompilationError> {
        let start_span = self.consume_kind(TokenKind::Struct)?;
        let name = self.consume_identifier()?;
        self.consume_kind(TokenKind::LeftBrace)?;

        let mut fields = Vec::new();
        while self.peek().kind != TokenKind::RightBrace && !self.is_at_end() {
            let field_start = self.current_span();
            let field_name = self.consume_identifier()?;
            self.consume_kind(TokenKind::Colon)?;
            let ty = self.parse_type()?;

            let span = Span::merge(field_start, ty_span(&ty));

            fields.push(Field {
                name: field_name,
                ty,
                span,
            });

            if self.peek().kind == TokenKind::Comma {
                self.advance();
            } else if self.peek().kind != TokenKind::RightBrace {
                return Err(CompilationError::error(
                    "Expected ',' or '}'",
                    self.current_span(),
                    "",
                ));
            }
        }

        self.consume_kind(TokenKind::RightBrace)?;

        let span = Span::merge(start_span, self.prev_span());
        Ok(StructDef {
            name,
            fields,
            span,
        })
    }

    fn parse_class(&mut self) -> Result<ClassDef, CompilationError> {
        let start_span = self.consume_kind(TokenKind::Class)?;
        let func = self.parse_function()?;
        let span = Span::merge(start_span, func.span);

        Ok(ClassDef {
            name: func.name.clone(),
            constructor: func,
            span,
        })
    }

    // ===== Type Parsing =====

    fn parse_type(&mut self) -> Result<Type, CompilationError> {
        // Handle references
        if self.peek().kind == TokenKind::Ampersand {
            self.advance();
            if self.peek().kind == TokenKind::Mut {
                self.advance();
                let inner = self.parse_type()?;
                return Ok(Type::MutableReference(Box::new(inner)));
            } else {
                let inner = self.parse_type()?;
                return Ok(Type::Reference(Box::new(inner)));
            }
        }

        // Parse base type - can be identifier or keyword (I32, I64, etc.)
        let base = match &self.peek().kind {
            TokenKind::Identifier(name) => name.clone(),
            TokenKind::I32 => "I32".to_string(),
            TokenKind::I64 => "I64".to_string(),
            TokenKind::F32 => "F32".to_string(),
            TokenKind::F64 => "F64".to_string(),
            TokenKind::String => "String".to_string(),
            TokenKind::Bool => "Bool".to_string(),
            TokenKind::Void => "Void".to_string(),
            _ => {
                return Err(CompilationError::error(
                    format!("Expected type but found '{}'", self.peek().kind),
                    self.current_span(),
                    "",
                ));
            }
        };

        self.advance();

        // Handle generics
        if self.peek().kind == TokenKind::Less {
            self.advance();
            let mut args = Vec::new();
            args.push(self.parse_type()?);

            while self.peek().kind == TokenKind::Comma {
                self.advance();
                args.push(self.parse_type()?);
            }

            self.consume_kind(TokenKind::Greater)?;
            Ok(Type::Generic(base, args))
        } else if self.peek().kind == TokenKind::LeftBracket {
            self.advance();
            let inner = self.parse_type()?;
            self.consume_kind(TokenKind::RightBracket)?;
            Ok(Type::Array(Box::new(inner)))
        } else {
            Ok(Type::Named(base))
        }
    }

    // ===== Expression Parsing =====

    fn parse_expression(&mut self) -> Result<Expression, CompilationError> {
        self.parse_logical_or()
    }

    fn parse_logical_or(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_logical_and()?;

        while self.peek().kind == TokenKind::PipePipe {
            let op_span = self.current_span();
            self.advance();
            let right = self.parse_logical_and()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                BinaryOp::Or,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_logical_and(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_bitwise_or()?;

        while self.peek().kind == TokenKind::AmpersandAmpersand {
            let op_span = self.current_span();
            self.advance();
            let right = self.parse_bitwise_or()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                BinaryOp::And,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_bitwise_or(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_bitwise_xor()?;

        while self.peek().kind == TokenKind::Pipe {
            let op_span = self.current_span();
            self.advance();
            let right = self.parse_bitwise_xor()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                BinaryOp::BitwiseOr,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_bitwise_xor(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_bitwise_and()?;

        while self.peek().kind == TokenKind::Caret {
            let op_span = self.current_span();
            self.advance();
            let right = self.parse_bitwise_and()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                BinaryOp::BitwiseXor,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_bitwise_and(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_equality()?;

        while self.peek().kind == TokenKind::Ampersand && self.peek_ahead(1).kind != TokenKind::Ampersand && self.peek_ahead(1).kind != TokenKind::Mut {
            let op_span = self.current_span();
            self.advance();
            let right = self.parse_equality()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                BinaryOp::BitwiseAnd,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_equality(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_comparison()?;

        loop {
            let op = match &self.peek().kind {
                TokenKind::EqualEqual => BinaryOp::Eq,
                TokenKind::BangEqual => BinaryOp::NotEq,
                _ => break,
            };

            let op_span = self.current_span();
            self.advance();
            let right = self.parse_comparison()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                op,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_comparison(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_shift()?;

        loop {
            let op = match &self.peek().kind {
                TokenKind::Less => BinaryOp::Less,
                TokenKind::LessEqual => BinaryOp::LessEq,
                TokenKind::Greater => BinaryOp::Greater,
                TokenKind::GreaterEqual => BinaryOp::GreaterEq,
                _ => break,
            };

            let op_span = self.current_span();
            self.advance();
            let right = self.parse_shift()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                op,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_shift(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_additive()?;

        loop {
            let op = match &self.peek().kind {
                TokenKind::LeftShift => BinaryOp::LeftShift,
                TokenKind::RightShift => BinaryOp::RightShift,
                _ => break,
            };

            let op_span = self.current_span();
            self.advance();
            let right = self.parse_additive()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                op,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_additive(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_multiplicative()?;

        loop {
            let op = match &self.peek().kind {
                TokenKind::Plus => BinaryOp::Add,
                TokenKind::Minus => BinaryOp::Sub,
                _ => break,
            };

            let op_span = self.current_span();
            self.advance();
            let right = self.parse_multiplicative()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                op,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_multiplicative(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_unary()?;

        loop {
            let op = match &self.peek().kind {
                TokenKind::Star => BinaryOp::Mul,
                TokenKind::Slash => BinaryOp::Div,
                TokenKind::Percent => BinaryOp::Mod,
                _ => break,
            };

            let op_span = self.current_span();
            self.advance();
            let right = self.parse_unary()?;
            let span = Span::merge(expr.span(), op_span);
            expr = Expression::Binary(
                op,
                Box::new(expr),
                Box::new(right),
                span,
            );
        }

        Ok(expr)
    }

    fn parse_unary(&mut self) -> Result<Expression, CompilationError> {
        match &self.peek().kind {
            TokenKind::Minus => {
                let op_span = self.current_span();
                self.advance();
                let expr = self.parse_unary()?;
                let span = Span::merge(op_span, expr.span());
                Ok(Expression::Unary(
                    UnaryOp::Neg,
                    Box::new(expr),
                    span,
                ))
            }
            TokenKind::Bang => {
                let op_span = self.current_span();
                self.advance();
                let expr = self.parse_unary()?;
                let span = Span::merge(op_span, expr.span());
                Ok(Expression::Unary(
                    UnaryOp::Not,
                    Box::new(expr),
                    span,
                ))
            }
            TokenKind::Ampersand => {
                if self.peek_ahead(1).kind == TokenKind::Mut {
                    let op_span = self.current_span();
                    self.advance();
                    self.advance();
                    let expr = self.parse_unary()?;
                    let span = Span::merge(op_span, expr.span());
                    Ok(Expression::Unary(
                        UnaryOp::MutableReference,
                        Box::new(expr),
                        span,
                    ))
                } else {
                    let op_span = self.current_span();
                    self.advance();
                    let expr = self.parse_unary()?;
                    let span = Span::merge(op_span, expr.span());
                    Ok(Expression::Unary(
                        UnaryOp::Reference,
                        Box::new(expr),
                        span,
                    ))
                }
            }
            TokenKind::Star => {
                let op_span = self.current_span();
                self.advance();
                let expr = self.parse_unary()?;
                let span = Span::merge(op_span, expr.span());
                Ok(Expression::Unary(
                    UnaryOp::Dereference,
                    Box::new(expr),
                    span,
                ))
            }
            _ => self.parse_postfix(),
        }
    }

    fn parse_postfix(&mut self) -> Result<Expression, CompilationError> {
        let mut expr = self.parse_primary()?;

        loop {
            match &self.peek().kind {
                TokenKind::LeftParen => {
                    self.advance();
                    let mut args = Vec::new();
                    if self.peek().kind != TokenKind::RightParen {
                        args.push(self.parse_expression()?);
                        while self.peek().kind == TokenKind::Comma {
                            self.advance();
                            args.push(self.parse_expression()?);
                        }
                    }
                    let close_span = self.consume_kind(TokenKind::RightParen)?;
                    let span = Span::merge(expr.span(), close_span);
                    expr = Expression::Call(
                        Box::new(expr),
                        args,
                        span,
                    );
                }
                TokenKind::Dot => {
                    self.advance();
                    let field = self.consume_identifier()?;
                    let span = expr.span();
                    expr = Expression::FieldAccess(
                        Box::new(expr),
                        field,
                        span,
                    );
                }
                TokenKind::LeftBracket => {
                    self.advance();
                    let index = self.parse_expression()?;
                    let close_span = self.consume_kind(TokenKind::RightBracket)?;
                    let span = Span::merge(expr.span(), close_span);
                    expr = Expression::Index(
                        Box::new(expr),
                        Box::new(index),
                        span,
                    );
                }
                _ => break,
            }
        }

        Ok(expr)
    }

    fn parse_primary(&mut self) -> Result<Expression, CompilationError> {
        let span = self.current_span();

        match &self.peek().kind {
            TokenKind::Integer(n) => {
                let n = *n;
                self.advance();
                Ok(Expression::Literal(Literal::Integer(n), span))
            }
            TokenKind::Float(f) => {
                let f = *f;
                self.advance();
                Ok(Expression::Literal(Literal::Float(f), span))
            }
            TokenKind::StringLiteral(s) => {
                let s = s.clone();
                self.advance();
                Ok(Expression::Literal(Literal::String(s), span))
            }
            TokenKind::True => {
                self.advance();
                Ok(Expression::Literal(Literal::Bool(true), span))
            }
            TokenKind::False => {
                self.advance();
                Ok(Expression::Literal(Literal::Bool(false), span))
            }
            TokenKind::Identifier(name) => {
                let name = name.clone();
                self.advance();
                Ok(Expression::Identifier(name, span))
            }
            TokenKind::LeftParen => {
                self.advance();
                let expr = self.parse_expression()?;
                self.consume_kind(TokenKind::RightParen)?;
                Ok(expr)
            }
            TokenKind::If => self.parse_if_expression(),
            TokenKind::LeftBrace => self.parse_block(),
            _ => Err(CompilationError::error(
                format!("Unexpected token: {}", self.peek().kind),
                span,
                "",
            )),
        }
    }

    fn parse_if_expression(&mut self) -> Result<Expression, CompilationError> {
        let start_span = self.consume_kind(TokenKind::If)?;
        let condition = Box::new(self.parse_expression()?);
        let then_branch = Box::new(self.parse_primary()?);

        let else_branch = if self.peek().kind == TokenKind::Else {
            self.advance();
            Some(Box::new(self.parse_primary()?))
        } else {
            None
        };

        let span = Span::merge(
            start_span,
            else_branch
                .as_ref()
                .map(|e| e.span())
                .unwrap_or_else(|| then_branch.span()),
        );

        Ok(Expression::IfExpr {
            condition,
            then_branch,
            else_branch,
            span,
        })
    }

    fn parse_block(&mut self) -> Result<Expression, CompilationError> {
        let start_span = self.consume_kind(TokenKind::LeftBrace)?;
        let mut exprs = Vec::new();

        while self.peek().kind != TokenKind::RightBrace && !self.is_at_end() {
            exprs.push(self.parse_expression()?);
            if self.peek().kind == TokenKind::Semicolon {
                self.advance();
            } else if self.peek().kind != TokenKind::RightBrace {
                break;
            }
        }

        let end_span = self.consume_kind(TokenKind::RightBrace)?;
        Ok(Expression::Block(exprs, Span::merge(start_span, end_span)))
    }

    // ===== Utilities =====

    fn peek(&self) -> &Token {
        self.tokens
            .get(self.position)
            .unwrap_or_else(|| &self.tokens[self.tokens.len() - 1])
    }

    fn peek_ahead(&self, n: usize) -> &Token {
        self.tokens
            .get(self.position + n)
            .unwrap_or_else(|| &self.tokens[self.tokens.len() - 1])
    }

    fn advance(&mut self) {
        if !self.is_at_end() {
            self.position += 1;
        }
    }

    fn is_at_end(&self) -> bool {
        self.peek().kind == TokenKind::Eof
    }

    fn current_span(&self) -> Span {
        self.peek().span
    }

    fn prev_span(&self) -> Span {
        if self.position > 0 {
            self.tokens[self.position - 1].span
        } else {
            self.current_span()
        }
    }

    fn consume_kind(&mut self, kind: TokenKind) -> Result<Span, CompilationError> {
        if std::mem::discriminant(&self.peek().kind) == std::mem::discriminant(&kind) {
            let span = self.current_span();
            self.advance();
            Ok(span)
        } else {
            Err(CompilationError::error(
                format!("Expected '{}' but found '{}'", kind, self.peek().kind),
                self.current_span(),
                "",
            ))
        }
    }

    fn consume_identifier(&mut self) -> Result<String, CompilationError> {
        match &self.peek().kind {
            TokenKind::Identifier(name) => {
                let name = name.clone();
                self.advance();
                Ok(name)
            }
            _ => Err(CompilationError::error(
                format!("Expected identifier but found '{}'", self.peek().kind),
                self.current_span(),
                "",
            )),
        }
    }

    fn synchronize(&mut self) {
        self.advance();

        while !self.is_at_end() {
            if matches!(
                self.peek().kind,
                TokenKind::Fn | TokenKind::Struct | TokenKind::Class
            ) {
                return;
            }

            self.advance();
        }
    }
}

// ===== Helper Functions =====

fn ty_span(_ty: &Type) -> Span {
    // Dummy span - in a real implementation, types would track their span
    Span::new(0, 0, 0, 0)
}

impl Item {
    fn span(&self) -> Span {
        match self {
            Item::FunctionDef(f) => f.span,
            Item::StructDef(s) => s.span,
            Item::ClassDef(c) => c.span,
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::lexer::Lexer;

    fn parse_expr(input: &str) -> Result<Expression, Vec<CompilationError>> {
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize()?;
        let mut parser = Parser::new(tokens);
        parser.parse_expression().map_err(|e| vec![e])
    }

    fn parse_prog(input: &str) -> Result<Program, Vec<CompilationError>> {
        let lexer = Lexer::new(input);
        let tokens = lexer.tokenize()?;
        let parser = Parser::new(tokens);
        parser.parse_program()
    }

    // ===== Literal Tests =====

    #[test]
    fn test_integer_literal() {
        let expr = parse_expr("42").unwrap();
        match expr {
            Expression::Literal(Literal::Integer(42), _) => {}
            _ => panic!("Expected integer literal"),
        }
    }

    #[test]
    fn test_float_literal() {
        let expr = parse_expr("3.14").unwrap();
        match expr {
            Expression::Literal(Literal::Float(f), _) if (f - 3.14).abs() < 0.001 => {}
            _ => panic!("Expected float literal"),
        }
    }

    #[test]
    fn test_string_literal() {
        let expr = parse_expr(r#""hello""#).unwrap();
        match expr {
            Expression::Literal(Literal::String(s), _) if s == "hello" => {}
            _ => panic!("Expected string literal"),
        }
    }

    #[test]
    fn test_bool_true_literal() {
        let expr = parse_expr("true").unwrap();
        match expr {
            Expression::Literal(Literal::Bool(true), _) => {}
            _ => panic!("Expected true literal"),
        }
    }

    #[test]
    fn test_bool_false_literal() {
        let expr = parse_expr("false").unwrap();
        match expr {
            Expression::Literal(Literal::Bool(false), _) => {}
            _ => panic!("Expected false literal"),
        }
    }

    // ===== Identifier Tests =====

    #[test]
    fn test_identifier() {
        let expr = parse_expr("foo").unwrap();
        match expr {
            Expression::Identifier(name, _) if name == "foo" => {}
            _ => panic!("Expected identifier"),
        }
    }

    // ===== Unary Operator Tests =====

    #[test]
    fn test_unary_negation() {
        let expr = parse_expr("-42").unwrap();
        match expr {
            Expression::Unary(UnaryOp::Neg, _, _) => {}
            _ => panic!("Expected unary negation"),
        }
    }

    #[test]
    fn test_unary_not() {
        let expr = parse_expr("!true").unwrap();
        match expr {
            Expression::Unary(UnaryOp::Not, _, _) => {}
            _ => panic!("Expected unary not"),
        }
    }

    #[test]
    fn test_unary_reference() {
        let expr = parse_expr("&x").unwrap();
        match expr {
            Expression::Unary(UnaryOp::Reference, _, _) => {}
            _ => panic!("Expected unary reference"),
        }
    }

    #[test]
    fn test_unary_mutable_reference() {
        let expr = parse_expr("&mut x").unwrap();
        match expr {
            Expression::Unary(UnaryOp::MutableReference, _, _) => {}
            _ => panic!("Expected unary mutable reference"),
        }
    }

    #[test]
    fn test_unary_dereference() {
        let expr = parse_expr("*ptr").unwrap();
        match expr {
            Expression::Unary(UnaryOp::Dereference, _, _) => {}
            _ => panic!("Expected unary dereference"),
        }
    }

    // ===== Binary Operator Tests =====

    #[test]
    fn test_addition() {
        let expr = parse_expr("1 + 2").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Add, _, _, _) => {}
            _ => panic!("Expected addition"),
        }
    }

    #[test]
    fn test_subtraction() {
        let expr = parse_expr("5 - 3").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Sub, _, _, _) => {}
            _ => panic!("Expected subtraction"),
        }
    }

    #[test]
    fn test_multiplication() {
        let expr = parse_expr("3 * 4").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Mul, _, _, _) => {}
            _ => panic!("Expected multiplication"),
        }
    }

    #[test]
    fn test_division() {
        let expr = parse_expr("8 / 2").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Div, _, _, _) => {}
            _ => panic!("Expected division"),
        }
    }

    #[test]
    fn test_modulo() {
        let expr = parse_expr("7 % 3").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Mod, _, _, _) => {}
            _ => panic!("Expected modulo"),
        }
    }

    #[test]
    fn test_equality() {
        let expr = parse_expr("a == b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Eq, _, _, _) => {}
            _ => panic!("Expected equality"),
        }
    }

    #[test]
    fn test_inequality() {
        let expr = parse_expr("a != b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::NotEq, _, _, _) => {}
            _ => panic!("Expected inequality"),
        }
    }

    #[test]
    fn test_less_than() {
        let expr = parse_expr("a < b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Less, _, _, _) => {}
            _ => panic!("Expected less than"),
        }
    }

    #[test]
    fn test_less_equal() {
        let expr = parse_expr("a <= b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::LessEq, _, _, _) => {}
            _ => panic!("Expected less equal"),
        }
    }

    #[test]
    fn test_greater_than() {
        let expr = parse_expr("a > b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Greater, _, _, _) => {}
            _ => panic!("Expected greater than"),
        }
    }

    #[test]
    fn test_greater_equal() {
        let expr = parse_expr("a >= b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::GreaterEq, _, _, _) => {}
            _ => panic!("Expected greater equal"),
        }
    }

    #[test]
    fn test_logical_and() {
        let expr = parse_expr("a && b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::And, _, _, _) => {}
            _ => panic!("Expected logical and"),
        }
    }

    #[test]
    fn test_logical_or() {
        let expr = parse_expr("a || b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Or, _, _, _) => {}
            _ => panic!("Expected logical or"),
        }
    }

    #[test]
    fn test_bitwise_and() {
        let expr = parse_expr("a & b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::BitwiseAnd, _, _, _) => {}
            _ => panic!("Expected bitwise and"),
        }
    }

    #[test]
    fn test_bitwise_or() {
        let expr = parse_expr("a | b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::BitwiseOr, _, _, _) => {}
            _ => panic!("Expected bitwise or"),
        }
    }

    #[test]
    fn test_bitwise_xor() {
        let expr = parse_expr("a ^ b").unwrap();
        match expr {
            Expression::Binary(BinaryOp::BitwiseXor, _, _, _) => {}
            _ => panic!("Expected bitwise xor"),
        }
    }

    #[test]
    fn test_left_shift() {
        let expr = parse_expr("a << 2").unwrap();
        match expr {
            Expression::Binary(BinaryOp::LeftShift, _, _, _) => {}
            _ => panic!("Expected left shift"),
        }
    }

    #[test]
    fn test_right_shift() {
        let expr = parse_expr("a >> 2").unwrap();
        match expr {
            Expression::Binary(BinaryOp::RightShift, _, _, _) => {}
            _ => panic!("Expected right shift"),
        }
    }

    // ===== Operator Precedence Tests =====

    #[test]
    fn test_mul_before_add() {
        // Should parse as (1 + (2 * 3)) = 7, not ((1 + 2) * 3) = 9
        let expr = parse_expr("1 + 2 * 3").unwrap();
        if let Expression::Binary(BinaryOp::Add, _, right, _) = expr {
            if let Expression::Binary(BinaryOp::Mul, _, _, _) = *right {
                // Correct: addition at top level, multiplication in right subtree
            } else {
                panic!("Expected multiplication in right subtree");
            }
        } else {
            panic!("Expected addition at top level");
        }
    }

    #[test]
    fn test_precedence_chain() {
        // 1 + 2 * 3 - 4 / 5
        let expr = parse_expr("1 + 2 * 3 - 4 / 5").unwrap();
        if let Expression::Binary(BinaryOp::Sub, _, _, _) = expr {
            // Correct: subtraction at top level
        } else {
            panic!("Expected subtraction at top level");
        }
    }

    // ===== Parentheses Tests =====

    #[test]
    fn test_parentheses_override_precedence() {
        // (1 + 2) * 3 should be 9, not 7
        let expr = parse_expr("(1 + 2) * 3").unwrap();
        if let Expression::Binary(BinaryOp::Mul, left, _, _) = expr {
            if let Expression::Binary(BinaryOp::Add, _, _, _) = *left {
                // Correct: addition in left subtree
            } else {
                panic!("Expected addition in left subtree");
            }
        } else {
            panic!("Expected multiplication at top level");
        }
    }

    // ===== Function Definition Tests =====

    #[test]
    fn test_simple_function() {
        let prog = parse_prog("fn add(a : I32, b : I32) : I32 => a + b;").unwrap();
        assert_eq!(prog.items.len(), 1);
        if let Item::FunctionDef(func) = &prog.items[0] {
            assert_eq!(func.name, "add");
            assert_eq!(func.params.len(), 2);
            assert_eq!(func.params[0].name, "a");
            assert_eq!(func.params[1].name, "b");
        } else {
            panic!("Expected function definition");
        }
    }

    #[test]
    fn test_function_with_no_params() {
        let prog = parse_prog("fn zero() : I32 => 0;").unwrap();
        if let Item::FunctionDef(func) = &prog.items[0] {
            assert_eq!(func.params.len(), 0);
        } else {
            panic!("Expected function definition");
        }
    }

    // ===== Struct Definition Tests =====

    #[test]
    fn test_simple_struct() {
        let prog = parse_prog(
            "struct Point {\n  x : I32,\n  y : I32\n}"
        ).unwrap();
        assert_eq!(prog.items.len(), 1);
        if let Item::StructDef(struct_def) = &prog.items[0] {
            assert_eq!(struct_def.name, "Point");
            assert_eq!(struct_def.fields.len(), 2);
        } else {
            panic!("Expected struct definition");
        }
    }

    // ===== Call Expression Tests =====

    #[test]
    fn test_function_call() {
        let expr = parse_expr("foo()").unwrap();
        match expr {
            Expression::Call(_, args, _) => {
                assert_eq!(args.len(), 0);
            }
            _ => panic!("Expected call expression"),
        }
    }

    #[test]
    fn test_function_call_with_args() {
        let expr = parse_expr("add(1, 2)").unwrap();
        match expr {
            Expression::Call(_, args, _) => {
                assert_eq!(args.len(), 2);
            }
            _ => panic!("Expected call expression"),
        }
    }

    // ===== Field Access Tests =====

    #[test]
    fn test_field_access() {
        let expr = parse_expr("point.x").unwrap();
        match expr {
            Expression::FieldAccess(_, field, _) if field == "x" => {}
            _ => panic!("Expected field access"),
        }
    }

    // ===== Index Tests =====

    #[test]
    fn test_array_index() {
        let expr = parse_expr("arr[0]").unwrap();
        match expr {
            Expression::Index(_, _, _) => {}
            _ => panic!("Expected index expression"),
        }
    }

    // ===== If Expression Tests =====

    #[test]
    fn test_if_expression() {
        let expr = parse_expr("if x > 0 { 1 } else { 2 }").unwrap();
        match expr {
            Expression::IfExpr {
                condition: _,
                then_branch: _,
                else_branch: Some(_),
                span: _,
            } => {}
            _ => panic!("Expected if expression"),
        }
    }

    // ===== Complex Expression Tests =====

    #[test]
    fn test_complex_arithmetic() {
        let expr = parse_expr("(a + b) * c - d / e").unwrap();
        match expr {
            Expression::Binary(BinaryOp::Sub, _, _, _) => {}
            _ => panic!("Expected subtraction at top level"),
        }
    }

    #[test]
    fn test_nested_function_calls() {
        let expr = parse_expr("add(mul(2, 3), 4)").unwrap();
        match expr {
            Expression::Call(_, args, _) => {
                assert_eq!(args.len(), 2);
            }
            _ => panic!("Expected call"),
        }
    }
}
