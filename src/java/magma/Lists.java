package magma;

class Lists {
    private Lists() {
    }

    public static <String> ListLike<String> empty() {
        return new JavaList<>();
    }
}
