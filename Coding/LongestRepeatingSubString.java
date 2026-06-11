//IP: "aaabbccccdde"
//
//OP: "cccc"

public static String longestRepeatingSubstring(String str) {

    if (str == null || str.isEmpty()) {
        return "";
    }

    int maxLen = 1;
    int currentLen = 1;

    int endIndex = 0;

    for (int i = 1; i < str.length(); i++) {

        if (str.charAt(i) == str.charAt(i - 1)) {
            currentLen++;
        } else {
            currentLen = 1;
        }

        if (currentLen > maxLen) {
            maxLen = currentLen;
            endIndex = i;
        }
    }

    return str.substring(endIndex - maxLen + 1, endIndex + 1);
}