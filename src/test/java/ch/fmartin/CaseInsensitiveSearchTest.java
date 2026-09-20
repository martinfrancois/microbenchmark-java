// Copyright 2026 François Martin
// SPDX-License-Identifier: Apache-2.0

package ch.fmartin;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.BiPredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

class CaseInsensitiveSearchTest {
    private static final List<BiPredicate<String, String>> SEARCHES = List.of(
            CaseInsensitiveSearch::lowercase,
            CaseInsensitiveSearch::ascii,
            CaseInsensitiveSearch::regionMatches);

    @Test
    void edgeCases() {
        check("", "", true);
        check("abc", "", true);
        check("", "a", false);
        check("a", "aa", false);
        check("abc", "ABC", true);
        check("aaaaab", "AAB", true);
        check("abc", "D", false);
        check("[\\]^_`", "{|}~", false);
        check("\0a\u007f", "\0A\u007f", true);
        check("prefix Needle suffix", "nEEdLE", true);
    }

    @Test
    void everySingleAsciiCharacterPair() {
        for (char haystack = 0; haystack < 128; haystack++) {
            for (char needle = 0; needle < 128; needle++) {
                check(String.valueOf(haystack), String.valueOf(needle), fold(haystack) == fold(needle));
            }
        }
    }

    @Test
    void generatedSearchesAgreeWithAnIndependentAsciiOracle() {
        Random random = new Random(42);
        for (int i = 0; i < 5_000; i++) {
            String haystack = randomAscii(random, random.nextInt(81));
            String needle = randomAscii(random, random.nextInt(31));
            check(haystack, needle, expected(haystack, needle));
            int start = random.nextInt(haystack.length() + 1);
            int end = start + random.nextInt(haystack.length() - start + 1);
            needle = haystack.substring(start, end).toUpperCase(Locale.ROOT);
            check(haystack, needle, true);
        }
    }

    @Test
    @ResourceLock("java.util.Locale.default")
    void turkishDefaultLocaleDoesNotChangeAsciiMatching() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            check("TITLE", "title", true);
            check("I", "i", true);
            check("INFINITY", "finite", false);
            for (SearchInputs.Input input : SearchInputs.create(20, 2, 4, "MIDDLE", 42)) {
                check(input.haystack(), input.needle(), input.expected());
            }
        } finally {
            Locale.setDefault(previous);
        }
    }

    @Test
    void nullInputsAreRejectedConsistently() {
        for (BiPredicate<String, String> search : SEARCHES) {
            assertThrows(NullPointerException.class, () -> search.test(null, "a"));
            assertThrows(NullPointerException.class, () -> search.test("a", null));
        }
    }

    @Test
    void unicodeIsOutsideTheSharedSemantics() {
        assertFalse(CaseInsensitiveSearch.ascii("\u0131", "i"));
        assertTrue(CaseInsensitiveSearch.regionMatches("\u0131", "i"));
    }

    @Test
    void allConfiguredFixturesHaveTheDeclaredMatchAndPosition() {
        for (int size : new int[]{20, 2000}) {
            for (int ratio : new int[]{2, 20}) {
                for (int length : new int[]{4, 16}) {
                    for (String position : new String[]{"START", "MIDDLE", "END", "ABSENT"}) {
                        for (long seed : new long[]{0xdeadbeefL, 42, 17}) {
                            SearchInputs.Input[] inputs = SearchInputs.create(size, ratio, length, position, seed);
                            assertEquals(SearchInputs.BATCH_SIZE, inputs.length);
                            for (SearchInputs.Input input : inputs) {
                                assertEquals(size, input.haystack().length());
                                assertEquals(length, input.needle().length());
                                assertAscii(input.haystack());
                                assertAscii(input.needle());
                                assertEquals(expected(input.haystack(), input.needle()), input.expected());
                                int expectedOffset = switch (position) {
                                    case "START" -> 0;
                                    case "MIDDLE" -> (size - length) / 2;
                                    case "END" -> size - length;
                                    default -> -1;
                                };
                                assertEquals(expectedOffset, input.haystack().toLowerCase(Locale.ROOT)
                                        .indexOf(input.needle().toLowerCase(Locale.ROOT)));
                                check(input.haystack(), input.needle(), input.expected());
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    void fixtureGenerationIsRepeatable() {
        assertArrayEquals(SearchInputs.create(2000, 20, 16, "ABSENT", 42),
                SearchInputs.create(2000, 20, 16, "ABSENT", 42));
    }

    private static void check(String haystack, String needle, boolean expected) {
        for (int i = 0; i < SEARCHES.size(); i++) {
            assertEquals(expected, SEARCHES.get(i).test(haystack, needle), "algorithm " + i);
        }
    }

    private static boolean expected(String haystack, String needle) {
        outer:
        for (int offset = 0; offset <= haystack.length() - needle.length(); offset++) {
            for (int i = 0; i < needle.length(); i++) {
                if (fold(haystack.charAt(offset + i)) != fold(needle.charAt(i))) continue outer;
            }
            return true;
        }
        return false;
    }

    private static char fold(char c) {
        return c >= 'A' && c <= 'Z' ? (char) (c + ('a' - 'A')) : c;
    }

    private static String randomAscii(Random random, int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) result.append((char) random.nextInt(128));
        return result.toString();
    }

    private static void assertAscii(String value) {
        for (int i = 0; i < value.length(); i++) assertTrue(value.charAt(i) < 128);
    }
}
