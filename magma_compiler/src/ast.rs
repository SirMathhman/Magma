#[derive(Debug, PartialEq, Clone)]
pub enum Node {
    Number(i64),
    Identifier(String),
}
