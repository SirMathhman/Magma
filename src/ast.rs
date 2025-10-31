/// Abstract Syntax Tree definitions for Magma programs
///
/// The AST is produced by the parser and represents the syntactic structure
/// of a Magma program with full source location information.
use crate::token::Span;

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

#[derive(Debug, Clone)]
pub enum Expression {
    Literal(Literal, Span),
    Identifier(String, Span),
    Binary(BinaryOp, Box<Expression>, Box<Expression>, Span),
    Unary(UnaryOp, Box<Expression>, Span),
}

#[derive(Debug, Clone)]
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
