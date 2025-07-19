# Project Roadmap

The roadmap tracks upcoming milestones. Items checked are completed.

- [ ] Set up project scaffolding and initial tests
- [x] Implement a minimal compiler skeleton
- [ ] Expand the language grammar
  - [x] Support `fn name() => {}` to `void name() {}`
  - [x] Handle multiple `fn` declarations in a single file
  - [x] Allow optional `Void` return type with `fn name(): Void => {}`
  - [x] Support boolean return with `fn name(): Bool => { return true; }` or
    `fn name(): Bool => { return false; }` generating
    `int name() { return 1; }` or `int name() { return 0; }` in C
- [ ] Build a self-hosted version of Magma

- [x] Set up CI/CD pipeline for running tests
