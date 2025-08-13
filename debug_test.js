function isEmptyStatement(s) { 
  return s.trim() === ''; 
}

function isStructDeclaration(s) { 
  return s.trim().startsWith('struct '); 
}

function isGenericFunctionDeclaration(s) {
  const trimmed = s.trim();
  let searchStart = 0;
  if (trimmed.startsWith('extern ')) {
    searchStart = 7;
  }
  
  if (!trimmed.slice(searchStart).startsWith('fn ')) return false;
  const fnIdx = searchStart + 3;
  const openParenIdx = trimmed.indexOf('(', fnIdx);
  if (openParenIdx === -1) return false;
  
  const name = trimmed.slice(fnIdx, openParenIdx).trim();
  return /<[A-Za-z0-9_,\s]+>$/.test(name);
}

function isFunctionDefinition(s) {
  const trimmed = s.trim();
  if (trimmed.startsWith('extern ')) {
    if (trimmed.indexOf('=>') !== -1) {
      throw new Error('extern functions cannot have bodies');
    }
    return false;
  }
  if (!trimmed.startsWith('fn ')) return false;
  const fnIdx = 3;
  const openParenIdx = trimmed.indexOf('(', fnIdx);
  const closeParenIdx = trimmed.indexOf(')', openParenIdx);
  const colonIdx = trimmed.indexOf(':', closeParenIdx);
  const arrowIdx = trimmed.indexOf('=>', colonIdx);
  const openBraceIdx = trimmed.indexOf('{', arrowIdx);
  const closeBraceIdx = trimmed.lastIndexOf('}');
  return openParenIdx !== -1 && closeParenIdx !== -1 && colonIdx !== -1 && 
         arrowIdx !== -1 && openBraceIdx !== -1 && closeBraceIdx !== -1;
}

function isFunctionPrototype(s) {
  return s.trim().endsWith(';') && s.includes('fn ') && s.indexOf('=>') === -1;
}

function isFunctionDeclaration(s) {
  return (isFunctionDefinition(s) || isFunctionPrototype(s)) && !isGenericFunctionDeclaration(s);
}

const statementTypeHandlers = [
  { type: 'empty', check: isEmptyStatement },
  { type: 'struct', check: isStructDeclaration },
  { type: 'generic-function', check: isGenericFunctionDeclaration },
  { type: 'function', check: isFunctionDeclaration }
];

function getStatementType(s) {
  for (const handler of statementTypeHandlers) {
    console.log(`Checking ${handler.type}:`, handler.check(s));
    if (handler.check(s)) return handler.type;
  }
  return 'unsupported';
}

const input = 'fn accept<T>(array : [T; 3]) : Void => {}';
console.log('Input:', input);
console.log('isGenericFunctionDeclaration:', isGenericFunctionDeclaration(input));
console.log('isFunctionDefinition:', isFunctionDefinition(input));
console.log('isFunctionDeclaration:', isFunctionDeclaration(input));
console.log('Final type:', getStatementType(input));