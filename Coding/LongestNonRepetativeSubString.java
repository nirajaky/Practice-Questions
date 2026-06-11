//Optimal Solution (Sliding Window)
//        Logic
//        Use two pointers (left, right) to maintain a window.
//        Use a Set to track characters currently in the window.
//        If a duplicate is found:
//        Remove characters from the left until the duplicate is gone.
//        Keep track of the maximum length and substring.

//IP: "abcabcbb"
//
//OP: abc

import java.util.HashSet;
import java.util.Set;

public class Main {

    public static String longestUniqueSubstring(String str) {

        Set<Character> set = new HashSet<>();

        int left = 0;
        int maxLen = 0;
        int endIndex = -1;

        for (int right = 0; right < str.length(); right++) {

            while (set.contains(str.charAt(right))) {
                set.remove(str.charAt(left++));
            }

            set.add(str.charAt(right));

            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                endIndex = right;
            }
        }

        return str.substring(endIndex - maxLen + 1, endIndex + 1);
    }

    public static void main(String[] args) {
        System.out.println(longestUniqueSubstring("abcabcbb"));
    }
}