Agent notes (breadcrumbs)
=========================

Purpose:
- A compact breadcrumb file the assistant reads before each task and updates after each task.

Design goals:
- Keep entries short and actionable — think "breadcrumbs", not a full timeline.
- Prefer latest-first, terse bullets with a one-line summary and optional 1-2 follow-up items.
- Avoid sensitive data or long dumps of code; link to files or classes instead of pasting contents.

Usage:
- The assistant will read this file before starting any task and append a new breadcrumb after completing the task.
- Contributors may edit the file, but please keep edits concise and factual. Do not remove or rename the file