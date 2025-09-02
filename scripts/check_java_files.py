#!/usr/bin/env python3
"""Check .java file counts in directories under src/main/java.

For each immediate directory inside `src/main/java` (and the root directory itself),
count the number of .java files directly inside that directory (do not recurse into
subdirectories). Exit with status 0 if all counts are <= limit (default 10). Otherwise
print offending directories and exit with status 1.

Usage:
  python scripts/check_java_files.py
  python scripts/check_java_files.py --limit 5
  python scripts/check_java_files.py --path path/to/src/main/java
"""
from pathlib import Path
import argparse
import sys
import os
import shlex


def count_java_files(directory: Path) -> int:
    return sum(1 for p in directory.iterdir() if p.is_file() and p.suffix == ".java")


def list_java_files(directory: Path):
    return sorted(
        [p for p in directory.iterdir() if p.is_file() and p.suffix == ".java"],
        key=lambda p: p.name,
    )


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(
        description="Verify no more than N .java files exist in directories under src/main/java (non-recursive)."
    )
    parser.add_argument(
        "--path",
        "-p",
        default="src/main/java",
        help="Base path to check (default: src/main/java)",
    )
    parser.add_argument(
        "--limit",
        "-l",
        type=int,
        default=10,
        help="Max allowed .java files per directory (default: 10)",
    )
    parser.add_argument(
        "--root-only",
        action="store_true",
        help="Only check the base directory, do not inspect immediate subdirectories",
    )
    args = parser.parse_args(argv)

    base = Path(args.path)
    if not base.exists():
        print(f"ERROR: base path does not exist: {base}", file=sys.stderr)
        return 2
    if not base.is_dir():
        print(f"ERROR: base path is not a directory: {base}", file=sys.stderr)
        return 2

    results = []
    # Check the base directory itself
    try:
        root_count = count_java_files(base)
    except PermissionError as e:
        print(f"ERROR: cannot access {base}: {e}", file=sys.stderr)
        return 2
    results.append((str(base), root_count))

    # Optionally check immediate subdirectories
    if not args.root_only:
        for entry in sorted(base.iterdir()):
            if entry.is_dir():
                try:
                    c = count_java_files(entry)
                except PermissionError as e:
                    print(f"ERROR: cannot access {entry}: {e}", file=sys.stderr)
                    return 2
                results.append((str(entry), c))

    failing = [(p, c) for (p, c) in results if c > args.limit]

    # Print a compact report
    for path, count in results:
        marker = "OK" if count <= args.limit else "TOO MANY"
        print(f"{marker:8} {count:3d}  {path}")

    if failing:
        print(
            "\nFailure: the following directories exceed the limit of",
            args.limit,
            "java files:",
        )
        for p, c in failing:
            print(f" - {p}: {c} .java files")

        # For each failing directory, suggest a small refactor using subdirectories.
        print("\nSuggested refactoring commands (PowerShell on Windows - pwsh):")
        for idx, (p, c) in enumerate(failing, start=1):
            dir_path = Path(p)
            files = list_java_files(dir_path)
            to_move = c - args.limit
            if to_move <= 0 or not files:
                continue
            files_to_move = [f.name for f in files[:to_move]]
            subdir = f"refactor_{idx}"
            # PowerShell commands
            print(f"\n# For directory: {p}")
            print(
                f'New-Item -ItemType Directory -Path "{dir_path.joinpath(subdir)}" -Force'
            )
            # Move files (PowerShell): provide a single Move-Item with multiple sources
            ps_sources = ", ".join(f'"{dir_path.joinpath(fn)}"' for fn in files_to_move)
            print(
                f'Move-Item -Path {ps_sources} -Destination "{dir_path.joinpath(subdir)}\\"'
            )

            # Also show POSIX alternative (useful in CI containers)
            posix_sub = str(dir_path.as_posix() + "/" + subdir)
            posix_sources = " ".join(
                shlex.quote(str(dir_path.joinpath(fn))) for fn in files_to_move
            )
            print(f'mkdir -p "{posix_sub}"')
            print(f'mv {posix_sources} "{posix_sub}/"')

        # Exit non-zero to fail CI/build
        return 1

    print("\nAll directories are within the limit (<=", args.limit, ").")
    return 0


if __name__ == "__main__":
    sys.exit(main())
