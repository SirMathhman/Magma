/// Abstract Syntax Tree definitions for Magma programs
///
/// The AST is produced by the parser and represents the syntactic structure
/// of a Magma program with full source location information.
use crate::token::Span;
use std::fmt;

#[derive(Debug, Clone)]
pub struct Program {
    pub items: Vec<Item>,
    pub span: Span,
}

#[derive(Debug, Clone)]
pub enum Item {
    FunctionDef(FunctionDef),
    StructDef(StructDef),
    ClassDef(ClassDef),
}

#[derive(Debug, Clone)]
pub struct FunctionDef {
    pub name: String,
    pub params: Vec<Parameter>,
    pub return_type: Type,
    pub body: Expression,
    pub span: Span,
}

#[derive(Debug, Clone)]
pub struct Parameter {
    pub name: String,
    pub ty: Type,
    pub span: Span,
}

#[derive(Debug, Clone)]
pub struct StructDef {
    pub name: String,
    pub fields: Vec<Field>,
    pub span: Span,
}

#[derive(Debug, Clone)]
pub struct Field {
    pub name: String,
    pub ty: Type,
    pub span: Span,
}

#[derive(Debug, Clone)]
pub struct ClassDef {
    pub name: String,
    pub constructor: FunctionDef,
    pub span: Span,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum Type {
    Named(String),
    Reference(Box<Type>),
    MutableReference(Box<Type>),
    Generic(String, Vec<Type>),
    Tuple(Vec<Type>),
    Array(Box<Type>),
}

impl fmt::Display for Type {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            Type::Named(s) => write!(f, "{}", s),
            Type::Reference(t) => write!(f, "&{}", t),
            Type::MutableReference(t) => write!(f, "&mut {}", t),
            Type::Generic(name, args) => {
                write!(f, "{}<", name)?;
                for (i, arg) in args.iter().enumerate() {
                    if i > 0 {
                        write!(f, ", ")?;
                    }
                    write!(f, "{}", arg)?;
                }
                write!(f, ">")
            }
            Type::Tuple(types) => {
                write!(f, "(")?;
                for (i, ty) in types.iter().enumerate() {
                    if i > 0 {
                        write!(f, ", ")?;
                    }
                    write!(f, "{}", ty)?;
                }
                write!(f, ")")
            }
            Type::Array(t) => write!(f, "[{}]", t),
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum Expression {
    Literal(Literal, Span),
    Identifier(String, Span),
    Binary(BinaryOp, Box<Expression>, Box<Expression>, Span),
    Unary(UnaryOp, Box<Expression>, Span),
    Call(Box<Expression>, Vec<Expression>, Span),
    FieldAccess(Box<Expression>, String, Span),
    Index(Box<Expression>, Box<Expression>, Span),
    IfExpr {
        condition: Box<Expression>,
        then_branch: Box<Expression>,
        else_branch: Option<Box<Expression>>,
        span: Span,
    },
    WhileLoop {
        condition: Box<Expression>,
        body: Box<Expression>,
        span: Span,
    },
    ForLoop {
        variable: String,
        range_start: Box<Expression>,
        range_end: Box<Expression>,
        body: Box<Expression>,
        span: Span,
    },
    Break(Span),
    Continue(Span),
    Block(Vec<Expression>, Span),
}

impl Expression {
    pub fn span(&self) -> Span {
        match self {
            Expression::Literal(_, span) => *span,
            Expression::Identifier(_, span) => *span,
            Expression::Binary(_, _, _, span) => *span,
            Expression::Unary(_, _, span) => *span,
            Expression::Call(_, _, span) => *span,
            Expression::FieldAccess(_, _, span) => *span,
            Expression::Index(_, _, span) => *span,
            Expression::IfExpr { span, .. } => *span,
            Expression::WhileLoop { span, .. } => *span,
            Expression::ForLoop { span, .. } => *span,
            Expression::Break(span) => *span,
            Expression::Continue(span) => *span,
            Expression::Block(_, span) => *span,
        }
    }
}

#[derive(Debug, Clone, PartialEq)]
pub enum Literal {
    Integer(i64),
    Float(f64),
    String(String),
    Bool(bool),
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum BinaryOp {
    Add,
    Sub,
    Mul,
    Div,
    Mod,
    Eq,
    NotEq,
    Less,
    LessEq,
    Greater,
    GreaterEq,
    And,
    Or,
    BitwiseAnd,
    BitwiseOr,
    BitwiseXor,
    LeftShift,
    RightShift,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum UnaryOp {
    Neg,
    Not,
    BitwiseNot,
    Reference,
    MutableReference,
    Dereference,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_type_display() {
        assert_eq!(Type::Named("I32".to_string()).to_string(), "I32");
        assert_eq!(
            Type::Reference(Box::new(Type::Named("String".to_string()))).to_string(),
            "&String"
        );
    }
}
