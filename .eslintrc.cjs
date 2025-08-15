module.exports = {
  root: true,
  parser: '@typescript-eslint/parser',
  parserOptions: {
    project: './tsconfig.json',
    sourceType: 'module'
  },
  plugins: ['@typescript-eslint'],
  extends: ['eslint:recommended', 'plugin:@typescript-eslint/recommended'],
  rules: {
    // enforce cyclomatic complexity <= 10
    'complexity': ['error', { 'max': 10 }],
    // recommended small project rules
    '@typescript-eslint/no-unused-vars': ['warn', { 'argsIgnorePattern': '^_' }]
  }
};
