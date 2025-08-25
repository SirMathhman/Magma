#!/usr/bin/env python3
"""
Simple script to search for the literal string "null" in source files.
Prints: <path>:<line-number>: <line>

Usage: python scripts/find_nulls.py

Excludes common build or binary folders.
"""
import os
import sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
EXCLUDE_DIRS = {'.git', 'target', 'build', 'out', '.idea', '.venv', 'venv', '__pycache__'}

matches = []
file_count = 0
for dirpath, dirnames, filenames in os.walk(ROOT):
    # modify dirnames in-place to skip excluded directories
    dirnames[:] = [d for d in dirnames if d not in EXCLUDE_DIRS]
    for fname in filenames:
        # Only consider Java source files for this run
        if not fname.endswith('.java'):
            continue
        path = os.path.join(dirpath, fname)
        rel = os.path.relpath(path, ROOT)
        try:
            with open(path, 'rb') as f:
                data = f.read()
        except Exception:
            # skip unreadable files
            continue
        try:
            text = data.decode('utf-8')
        except UnicodeDecodeError:
            try:
                text = data.decode('latin-1')
            except Exception:
                continue
        found = False
        for i, line in enumerate(text.splitlines(), start=1):
            if 'null' in line:
                matches.append((rel, i, line.rstrip('\n')))
                found = True
        if found:
            file_count += 1

if not matches:
    print('No occurrences of "null" found in .java files.')
    sys.exit(0)

for rel, lineno, line in matches:
    print(f'{rel}:{lineno}: {line}')

print('\nSummary:')
print(f'Files with matches: {file_count}')
print(f'Total matches: {len(matches)}')

sys.exit(0)
