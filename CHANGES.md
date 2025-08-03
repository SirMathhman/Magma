# Changes

## 2025-08-02

### Added
- Added support for detecting and handling Java packages
- Added validation to ensure processing matches input configuration
- Added tests for Java package handling

### Changed
- Updated MapUtils.java to ensure Java packages don't produce any C output
- Replaced null usage with Optional throughout the codebase
- Consolidated redundant tests in MapUtilsTest.java

### Removed
- Removed redundant tests that were testing the same functionality in slightly different ways

## Notes
- We don't use null in this codebase, we use Optional instead.