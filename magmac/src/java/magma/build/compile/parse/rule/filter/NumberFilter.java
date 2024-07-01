package magma.build.compile.parse.rule.filter;

public class NumberFilter implements Filter {
    private static boolean allDigits(String input) {
        if (input.isEmpty()) return false;

        int i = 0;
        while (i < input.length()) {
            var c = input.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            i++;
        }

        return true;
    }


    @Override
    public String computeMessage() {
        return "Not a number.";
    }

    @Override
    public boolean filter(String input) {
        return input.startsWith("-")
                ? allDigits(input.substring(1))
                : allDigits(input);
    }
}
