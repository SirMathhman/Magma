#!/usr/bin/env python3
"""Check that each Java package in src/main/java and src/test/java contains at most N classes.

Usage: python scripts/check_package_sizes.py [--max N] [--root PATH]

Exits with code 0 when no violations, 1 when any package exceeds the limit.
"""
from __future__ import annotations

import argparse
import os
import sys
from collections import defaultdict


def find_packages(root: str, subdir: str) -> dict:
    base = os.path.join(root, subdir)
    packages = defaultdict(int)
    if not os.path.isdir(base):
        return packages
    for dirpath, dirnames, filenames in os.walk(base):
        # Count .java files in this directory as classes for the package represented by the dir
        java_files = [f for f in filenames if f.endswith('.java')]
        if java_files:
            # package name relative to base, convert path sep to dot
            rel = os.path.relpath(dirpath, base)
            if rel == '.' or rel == os.curdir:
                pkg = '(default)'
            else:
                pkg = rel.replace(os.path.sep, '.')
            packages[pkg] += len(java_files)
    return packages


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description='Ensure Java packages contain at most N classes')
    parser.add_argument('--max', '-m', type=int, default=10, help='Maximum classes allowed per package (default: 10)')
    parser.add_argument('--root', '-r', default='.', help='Repository root or module directory (default: current directory)')
    args = parser.parse_args(argv)

    root = args.root
    max_allowed = args.max

    violations = []

    for sub in ('src/main/java', 'src/test/java'):
        pkgs = find_packages(root, sub)
        for pkg, count in sorted(pkgs.items()):
            if count > max_allowed:
                violations.append((sub, pkg, count))

    if violations:
        print('Package size violations found:')
        for sub, pkg, count in violations:
            print(f" - {sub} package '{pkg}' has {count} classes (max {max_allowed})")
        print('\nFAIL: one or more packages exceed the allowed class count.')
        return 1

    print('OK: all packages are within the allowed class count.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
