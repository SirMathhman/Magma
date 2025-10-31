/// High-level Intermediate Representation (HIR)
///
/// Bridge between semantic analysis and code generation.
/// Simplified, ownership-annotated representation ready for backend lowering.
///
/// Key properties:
/// - Type-complete: All types resolved from semantic analysis
/// - Ownership-annotated: Borrows, references, and moves explicit
/// - Desugared: Complex expressions simplified to basic operations
/// - Span-preserved: Source locations maintained for debugging
use crate::semantic::ResolvedType;
use crate::token::Span;
use std::fmt;

/// A complete HIR program
#[derive(Debug, Clone)]
pub struct HirProgram {
    pub items: Vec<HirItem>,
}

/// Top-level program items
#[derive(Debug, Clone)]
pub enum HirItem {
    Function(HirFunction),
    Struct(HirStruct),
    Class(HirClass),
}

/// Function in HIR form
#[derive(Debug, Clone)]
pub struct HirFunction {
    pub name: String,
    pub params: Vec<HirParam>,
    pub return_type: ResolvedType,
    pub body: HirExpr,
    pub span: Span,
}

/// Function parameter
#[derive(Debug, Clone)]
pub struct HirParam {
    pub name: String,
    pub ty: ResolvedType,
    pub ownership: Ownership,
    pub span: Span,
}

/// Struct in HIR form
#[derive(Debug, Clone)]
pub struct HirStruct {
    pub name: String,
    pub fields: Vec<HirField>,
    pub span: Span,
}

/// Struct field
#[derive(Debug, Clone)]
pub struct HirField {
    pub name: String,
    pub ty: ResolvedType,
    pub span: Span,
}

/// Class definition (constructor pattern)
#[derive(Debug, Clone)]
pub struct HirClass {
    pub name: String,
    pub constructor: HirFunction,
    pub span: Span,
}

/// Ownership annotation for values
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Ownership {
    /// Owned value - has exclusive access, responsible for cleanup
    Owned,
    /// Immutable borrow - read-only reference, caller retains ownership
    Borrowed,
    /// Mutable borrow - exclusive mutable reference, caller retains ownership
    MutableBorrowed,
    /// Copy semantics - value is Copy type (primitives)
    Copy,
}

impl fmt::Display for Ownership {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            Ownership::Owned => write!(f, "owned"),
            Ownership::Borrowed => write!(f, "borrowed"),
            Ownership::MutableBorrowed => write!(f, "mut_borrowed"),
            Ownership::Copy => write!(f, "copy"),
        }
    }
}

/// HIR expression - simplified and type-complete
#[derive(Debug, Clone)]
pub struct HirExpr {
    pub kind: HirExprKind,
    pub ty: ResolvedType,
    pub ownership: Ownership,
    pub span: Span,
}

/// Expression kinds in HIR
#[derive(Debug, Clone)]
pub enum HirExprKind {
    /// Literal value
    Literal(HirLiteral),

    /// Local variable reference
    Var(String),

    /// Binary operation
    Binary {
        op: HirBinaryOp,
        left: Box<HirExpr>,
        right: Box<HirExpr>,
    },

    /// Unary operation
    Unary { op: HirUnaryOp, expr: Box<HirExpr> },

    /// Function call
    Call { func: String, args: Vec<HirExpr> },

    /// If expression (desugared to select expression)
    If {
        cond: Box<HirExpr>,
        then_expr: Box<HirExpr>,
        else_expr: Option<Box<HirExpr>>,
    },

    /// Block (sequence of expressions, last is result)
    Block(Vec<HirExpr>),

    /// Reference creation
    Borrow { expr: Box<HirExpr>, mutable: bool },

    /// Dereference
    Deref(Box<HirExpr>),

    /// Field access
    FieldAccess { expr: Box<HirExpr>, field: String },

    /// Array indexing
    Index {
        expr: Box<HirExpr>,
        index: Box<HirExpr>,
    },

    /// Constructor call (creates struct instance)
    Constructor {
        name: String,
        fields: Vec<(String, HirExpr)>,
    },
}

/// Literals in HIR
#[derive(Debug, Clone)]
pub enum HirLiteral {
    Integer(i64),
    Float(f64),
    String(String),
    Bool(bool),
}

/// Binary operators in HIR
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum HirBinaryOp {
    // Arithmetic
    Add,
    Sub,
    Mul,
    Div,
    Mod,

    // Comparison
    Eq,
    NotEq,
    Less,
    LessEq,
    Greater,
    GreaterEq,

    // Logical
    And,
    Or,

    // Bitwise
    BitwiseAnd,
    BitwiseOr,
    BitwiseXor,
    LeftShift,
    RightShift,
}

impl fmt::Display for HirBinaryOp {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            HirBinaryOp::Add => write!(f, "+"),
            HirBinaryOp::Sub => write!(f, "-"),
            HirBinaryOp::Mul => write!(f, "*"),
            HirBinaryOp::Div => write!(f, "/"),
            HirBinaryOp::Mod => write!(f, "%"),
            HirBinaryOp::Eq => write!(f, "=="),
            HirBinaryOp::NotEq => write!(f, "!="),
            HirBinaryOp::Less => write!(f, "<"),
            HirBinaryOp::LessEq => write!(f, "<="),
            HirBinaryOp::Greater => write!(f, ">"),
            HirBinaryOp::GreaterEq => write!(f, ">="),
            HirBinaryOp::And => write!(f, "&&"),
            HirBinaryOp::Or => write!(f, "||"),
            HirBinaryOp::BitwiseAnd => write!(f, "&"),
            HirBinaryOp::BitwiseOr => write!(f, "|"),
            HirBinaryOp::BitwiseXor => write!(f, "^"),
            HirBinaryOp::LeftShift => write!(f, "<<"),
            HirBinaryOp::RightShift => write!(f, ">>"),
        }
    }
}

/// Unary operators in HIR
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum HirUnaryOp {
    Neg,
    Not,
    BitwiseNot,
    Reference,
    MutableReference,
    Deref,
}

impl fmt::Display for HirUnaryOp {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            HirUnaryOp::Neg => write!(f, "-"),
            HirUnaryOp::Not => write!(f, "!"),
            HirUnaryOp::BitwiseNot => write!(f, "~"),
            HirUnaryOp::Reference => write!(f, "&"),
            HirUnaryOp::MutableReference => write!(f, "&mut"),
            HirUnaryOp::Deref => write!(f, "*"),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_ownership_display() {
        assert_eq!(Ownership::Owned.to_string(), "owned");
        assert_eq!(Ownership::Borrowed.to_string(), "borrowed");
        assert_eq!(Ownership::MutableBorrowed.to_string(), "mut_borrowed");
        assert_eq!(Ownership::Copy.to_string(), "copy");
    }

    #[test]
    fn test_binary_op_display() {
        assert_eq!(HirBinaryOp::Add.to_string(), "+");
        assert_eq!(HirBinaryOp::Eq.to_string(), "==");
        assert_eq!(HirBinaryOp::And.to_string(), "&&");
    }

    #[test]
    fn test_unary_op_display() {
        assert_eq!(HirUnaryOp::Neg.to_string(), "-");
        assert_eq!(HirUnaryOp::Not.to_string(), "!");
        assert_eq!(HirUnaryOp::Reference.to_string(), "&");
    }

    #[test]
    fn test_hir_expr_creation() {
        let expr = HirExpr {
            kind: HirExprKind::Literal(HirLiteral::Integer(42)),
            ty: ResolvedType::I32,
            ownership: Ownership::Copy,
            span: crate::token::Span::new(0, 0, 0, 0),
        };
        assert_eq!(expr.ty, ResolvedType::I32);
    }
}
