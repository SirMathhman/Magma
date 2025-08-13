// Let's simulate the smartSplit function to understand how statements are split
function getDepth(str) {
  let bracketDepth = 0;
  let braceDepth = 0;

  for (let i = 0; i < str.length; i++) {
    const ch = str[i];
    if (ch === '[') bracketDepth++;
    else if (ch === ']') bracketDepth--;
    else if (ch === '{') braceDepth++;
    else if (ch === '}') braceDepth--;
  }

  return { bracketDepth, braceDepth };
}

function shouldSplitHere(ch, buf, bracketDepth, braceDepth, str, i) {
  // Split after a block if followed by non-whitespace
  if (ch === '}' && bracketDepth === 0 && braceDepth === 0) {
    let j = i + 1;
    while (j < str.length && /\s/.test(str[j])) j++;

    // Don't split if the next token is "else" (keep if-else together)
    if (j < str.length && str.slice(j).startsWith('else')) {
      return null;
    }

    if (j < str.length) return 'block';
  }
  return null;
}

function smartSplit(str) {
  const statements = [];
  let currentStatement = '';
  let bracketDepth = 0;
  let braceDepth = 0;

  for (let i = 0; i < str.length; i++) {
    const ch = str[i];
    currentStatement += ch;

    // Update depth tracking
    if (ch === '[') bracketDepth++;
    else if (ch === ']') bracketDepth--;
    else if (ch === '{') braceDepth++;
    else if (ch === '}') braceDepth--;

    // Check if we should split here
    const splitType = shouldSplitHere(ch, currentStatement, bracketDepth, braceDepth, str, i);
    if (splitType) {
      statements.push(currentStatement.trim());
      currentStatement = '';
    }
  }

  // Add any remaining statement
  if (currentStatement.trim()) {
    statements.push(currentStatement.trim());
  }

  return statements;
}

const input = 'fn accept<T>(array : [T; 3]) : Void => {} accept<I32>([1, 2, 3]);';
console.log('Input:', input);
console.log('Smart split result:', smartSplit(input));