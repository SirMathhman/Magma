struct Option {
    enum OptionTag tag;
    union {
        struct Some Some;
        struct None None;
    } data;
};
enum OptionTag { Some, None };
