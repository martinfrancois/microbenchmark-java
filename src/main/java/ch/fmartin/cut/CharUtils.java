package ch.fmartin.cut;

import java.util.Set;

public interface CharUtils {

    Set<Character> ILLEGAL_CHARS = Set.of('/', '\\', '<', '>', ':', '\"', '|', '?', '*', '\u007F');

    static String removeIllegalCharacters(String text, String replaceWith) {
        int startIndex = 0;
        int endIndex = text.length();
        StringBuilder sb = new StringBuilder();

        // find the index of when the string (excluding whitespaces) starts
        while ((startIndex < endIndex) && (text.charAt(startIndex) <= ' ')) {
            startIndex++;
        }

        // find the index of when the string (excluding whitespaces) ends
        while ((startIndex < endIndex) && (text.charAt(endIndex - 1) <= ' ')) {
            endIndex--;
        }

        // if there are whitespaces at the of the string, replace all of them with ONE `replaceWith`
        if (startIndex != 0) {
            sb.append(replaceWith);
        }

        // replace control and illegal characters in the string itself
        for (int i = startIndex; i < endIndex; i++) {
            char current = text.charAt(i);
            boolean isControlCharacter = current < ' ';
            if (isControlCharacter || ILLEGAL_CHARS.contains(current)) {
                sb.append(replaceWith);
            } else {
                sb.append(current);
            }
        }

        // if there are whitespaces at the end of the string, replace all of them with ONE `replaceWith`
        if (text.length() != endIndex) {
            sb.append(replaceWith);
        }

        return sb.toString();
    }
}
