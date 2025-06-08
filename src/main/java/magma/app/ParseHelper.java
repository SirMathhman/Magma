package magma.app;

class ParseHelper {
    static boolean isNumeric(String s) {
        if (s.isEmpty()) return false;
        var i = 0;
        if (s.charAt(0) == '-') {
            if (s.length() == 1) return false;
            i = 1;
        }
        var dot = false;
        for (; i < s.length(); i++) {
            var c = s.charAt(i);
            if (c == '.') {
                if (dot) return false;
                dot = true;
                continue;
            }
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
}
