// Copyright 2026 François Martin
// SPDX-License-Identifier: Apache-2.0

package ch.fmartin;

import java.util.Locale;
import java.util.Objects;

/** Comparisons share case-insensitive substring semantics for non-null ASCII strings only. */
final class CaseInsensitiveSearch {
    private CaseInsensitiveSearch() {}

    static boolean lowercase(String haystack, String needle) {
        Objects.requireNonNull(haystack);
        Objects.requireNonNull(needle);
        return haystack.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }

    static boolean ascii(String haystack, String needle) {
        Objects.requireNonNull(haystack);
        Objects.requireNonNull(needle);
        return Ascii.containsIgnoreCase(haystack, needle);
    }

    static boolean regionMatches(String haystack, String needle) {
        Objects.requireNonNull(haystack);
        Objects.requireNonNull(needle);
        int lastStart = haystack.length() - needle.length();
        for (int offset = 0; offset <= lastStart; offset++) {
            if (haystack.regionMatches(true, offset, needle, 0, needle.length())) {
                return true;
            }
        }
        return false;
    }
}
