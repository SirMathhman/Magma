#!/usr/bin/env python3
"""
Scan src/main/java and src/test/java for packages containing too many Java classes.
If any package has more than 10 .java files, print a message and exit with status 1.

This script is intended to be executed from the repository root by Maven.
"""
import os
import re
import sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
SRC_ROOTS = [
    os.path.join(ROOT, "src", "main", "java"),
    os.path.join(ROOT, "src", "test", "java"),
]
PACKAGE_RE = re.compile(r"^\s*package\s+([\w\.]+)\s*;")


def package_from_path(root, dirpath):
    rel = os.path.relpath(dirpath, root)
    parts = [p for p in rel.split(os.sep) if p and p not in ("main", "java")]
    if not parts or parts == ["."]:
        return "(default package)"
    return ".".join(parts)


def find_package_name(java_files, dirpath):
    for fname in java_files:
        path = os.path.join(dirpath, fname)
        try:
            with open(path, "r", encoding="utf-8") as f:
                for line in f:
                    m = PACKAGE_RE.match(line)
                    if m:
                        return m.group(1)
        except Exception:
            # ignore unreadable files
            pass
    return None


def scan_root(root):
    problems = []
    if not os.path.isdir(root):
        return problems
    for dirpath, dirs, files in os.walk(root):
        java_files = [f for f in files if f.endswith(".java")]
        if not java_files:
            continue
        count = len(java_files)
        if count > 10:
            pkg = find_package_name(java_files, dirpath)
            if not pkg:
                pkg = package_from_path(root, dirpath)
            problems.append((pkg, count, dirpath))
    return problems


def main():
    all_problems = []
    for root in SRC_ROOTS:
        all_problems.extend(scan_root(root))

    if not all_problems:
        print("Package size check: OK")
        return 0

    for pkg, count, path in all_problems:
        print(f"Too many classes! {pkg} ({count} .java files) at {path}")

    print("Failing build because one or more packages exceed the class limit (10). You should probably separate these into separate packages.")
    return 1


if __name__ == "__main__":
    sys.exit(main())
