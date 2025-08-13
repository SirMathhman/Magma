// Test function call detection
function isFunctionCall(s) {
  // Simplified version for testing
  const trimmed = s.trim();
  if (trimmed.includes('(') && trimmed.includes(')')) {
    // Check if it looks like a function call
    const parenIdx = trimmed.indexOf('(');
    const beforeParen = trimmed.slice(0, parenIdx).trim();
    
    // Should have a name (possibly with generic parameters) before the parentheses
    if (/^[a-zA-Z_][a-zA-Z0-9_<>,\s]*$/.test(beforeParen)) {
      return true;
    }
  }
  return false;
}

const statement = 'accept<I32>([1, 2, 3]);';
console.log('Statement:', statement);
console.log('Is function call:', isFunctionCall(statement));

// Test with a simpler pattern
const parenIdx = statement.indexOf('(');
console.log('Before paren:', statement.slice(0, parenIdx));
console.log('Regex test:', /^[a-zA-Z_][a-zA-Z0-9_<>,\s]*$/.test(statement.slice(0, parenIdx)));