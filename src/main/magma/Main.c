struct Wrapper_int32_t {
    int32_t value;
};

struct Wrapper_int32_t Wrapper_int32_t(int32_t value) {
    struct Wrapper_int32_t this;
    this.value = value;
    return this;
}

struct Wrapper_int32_t wrapper = Wrapper_int32_t(42);