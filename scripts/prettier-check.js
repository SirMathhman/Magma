import { execSync } from 'child_process';

try {
  execSync('npx prettier --check "src/**/*.{ts,js}" --write', { stdio: 'inherit' });
} catch (err) {
  console.error('Prettier check failed. Please format your code.');
  process.exit(1);
}
