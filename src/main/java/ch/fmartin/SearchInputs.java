// Copyright 2026 François Martin
// SPDX-License-Identifier: Apache-2.0

package ch.fmartin;

import java.util.Locale;
import java.util.Random;

final class SearchInputs {
    static final int BATCH_SIZE = 32;
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String OTHER = "0123456789`~-_=+[]{}|;:',.<>/?!@#$%^&*()";

    record Input(String haystack, String needle, boolean expected) {}

    private SearchInputs() {}

    static Input[] create(int size, int nonAlphaRatio, int needleLength, String position, long seed) {
        if (size < 1 || needleLength < 1 || needleLength > size || nonAlphaRatio < 1) {
            throw new IllegalArgumentException("Require size >= needleLength >= 1 and nonAlphaRatio >= 1");
        }
        int offset = switch (position) {
            case "START" -> 0;
            case "MIDDLE", "ABSENT" -> (size - needleLength) / 2;
            case "END" -> size - needleLength;
            default -> throw new IllegalArgumentException("Unknown position: " + position);
        };
        Random random = new Random(seed);
        Input[] inputs = new Input[BATCH_SIZE];
        for (int i = 0; i < inputs.length; i++) {
            String haystack;
            String needle;
            int attempts = 0;
            do {
                if (++attempts > 1_000) {
                    throw new IllegalArgumentException("Cannot generate a unique match; increase needleLength");
                }
                char[] chars = new char[size];
                int nonAlpha = size / nonAlphaRatio;
                for (int j = 0; j < chars.length; j++) {
                    String alphabet = j < nonAlpha ? OTHER : LETTERS;
                    chars[j] = alphabet.charAt(random.nextInt(alphabet.length()));
                }
                for (int j = chars.length - 1; j > 0; j--) {
                    int other = random.nextInt(j + 1);
                    char temp = chars[j];
                    chars[j] = chars[other];
                    chars[other] = temp;
                }
                haystack = new String(chars);
                needle = haystack.substring(offset, offset + needleLength).toUpperCase(Locale.ROOT);
                // A repeated substring would move the first match away from the requested position.
            } while (haystack.toLowerCase(Locale.ROOT).indexOf(needle.toLowerCase(Locale.ROOT)) != offset);
            boolean expected = !position.equals("ABSENT");
            if (!expected) {
                // NUL is ASCII and never generated in a haystack, guaranteeing a failed search.
                needle = needle.substring(0, needle.length() - 1) + '\0';
            }
            inputs[i] = new Input(haystack, needle, expected);
        }
        return inputs;
    }
}
