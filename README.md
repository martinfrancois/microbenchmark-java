# Case-insensitive ASCII substring search

This branch preserves François Martin's 2019 JMH comparison of three searches:

- Lowercase both strings with `Locale.ROOT`, then use `String.contains`.
- Use the ASCII-specific algorithm from [Guava PR #3023](https://github.com/google/guava/pull/3023).
- Scan with `String.regionMatches` and its ignore-case option.

All measured inputs contain ASCII characters only. Within that domain, the
algorithms share the same case-insensitive substring semantics. They reject
null inputs and accept an empty needle. They do not implement equivalent
Unicode searches. For example, `regionMatches` equates the dotless Turkish
letter i with ASCII i, while the ASCII algorithm does not.

## Build and check

Use JDK 21 or newer and Maven 3.9 or newer.

```sh
mvn clean verify
java -jar target/benchmarks.jar -l
```

Tests cover every single-character ASCII pair, empty inputs, repeated prefixes,
failed searches, seeded random inputs, Turkish default locale, and every
configured benchmark fixture with three seeds. Trial setup also verifies all
three algorithms against each fixture's declared result before timing.

Run a short execution check of every configured parameter combination:

```sh
java -jar target/benchmarks.jar 'ch.fmartin.PerformanceBenchmark.*' \
  -f 1 -wi 0 -i 1 -r 10ms -foe true
```

This checks execution only. Do not use its timings as performance evidence.

## Measure

Start with one workload:

```sh
java -jar target/benchmarks.jar 'ch.fmartin.PerformanceBenchmark.*' \
  -p size=2000 -p nonAlphaRatio=20 -p needleLength=16 -p position=ABSENT \
  -rf json -rff target/contains-ignore-case.json -foe true
```

Omit the parameter overrides to run the full matrix. The default configuration
has 96 method/parameter combinations, three fresh JVM forks, five one-second
warmup iterations, and five one-second measurement iterations. Allow roughly
48 minutes plus JVM startup and setup for the full matrix.

| Parameter | Default values | Meaning |
| --- | --- | --- |
| `size` | `20`, `2000` | Haystack length |
| `nonAlphaRatio` | `2`, `20` | One digit or punctuation character per this many haystack characters |
| `needleLength` | `4`, `16` | Search string length |
| `position` | `START`, `MIDDLE`, `END`, `ABSENT` | First match position, or a failed search |
| `seed` | `3735928559` | Repeatable input generation; override to test other datasets |

Each trial prepares 32 different input pairs. Present needles are uppercase
substrings of their haystacks. The generator rejects accidental earlier matches.
Absent needles retain a substring's prefix and replace its last character with
NUL, an ASCII character excluded from the generated haystacks. This makes failure
deterministic; it does not represent every real-world unsuccessful query.

Each invocation searches all 32 pairs and consumes every answer through a JMH
`Blackhole`. `@OperationsPerInvocation(32)` normalizes the score to nanoseconds
per search. Input construction and validation are outside measurement. The
lowercase method includes string conversion and allocation, as the original
experiment did; it is not a comparison of pre-normalized strings.

Repeat with different seeds and record the JDK, hardware and runtime flags.
Results apply to the chosen fixtures and JVM. For allocation measurements, add
`-prof gc`. Do not infer a universal winner from one case or from the smoke run.

## Preservation and provenance

The original experiment varied string length, punctuation density and batch
size. This version retains the three algorithms and seeded-input approach,
fixes locale handling, adds failed searches and controlled match positions, and
uses one fixed batch size with explicit score normalization. The old timings
are not carried forward as claims about current JDKs.

This experiment stays on `containsIgnoreCase`; the default branch remains the
empty JMH template. See [source and license notes](THIRD_PARTY_NOTICES.md) for
Guava attribution and the Apache license for this branch's new code.
