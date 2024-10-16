package ch.fmartin.cut;

public interface RegexUtils {

    static String removeIllegalCharacters(String text, String replaceString) {
        text = text.replaceAll("[\\p{Cntrl}/\\\\:\\*\\?\"<>|]", replaceString);
        text = text.replaceAll("(^[ \\t]+)|([ \\t]+$)", replaceString);
        return text;
    }
}
