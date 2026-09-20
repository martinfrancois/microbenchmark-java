// Copyright 2026 François Martin
// SPDX-License-Identifier: Apache-2.0

package ch.fmartin;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PerformanceBenchmark {
    @Param({"20", "2000"})
    public int size;

    @Param({"2", "20"})
    public int nonAlphaRatio;

    @Param({"4", "16"})
    public int needleLength;

    @Param({"START", "MIDDLE", "END", "ABSENT"})
    public String position;

    @Param({"3735928559"})
    public long seed;

    private SearchInputs.Input[] inputs;

    @Setup(Level.Trial)
    public void setup() {
        inputs = SearchInputs.create(size, nonAlphaRatio, needleLength, position, seed);
        for (SearchInputs.Input input : inputs) {
            if (CaseInsensitiveSearch.lowercase(input.haystack(), input.needle()) != input.expected()
                    || CaseInsensitiveSearch.ascii(input.haystack(), input.needle()) != input.expected()
                    || CaseInsensitiveSearch.regionMatches(input.haystack(), input.needle()) != input.expected()) {
                throw new IllegalStateException("Search implementations disagree with the fixture");
            }
        }
    }

    @Benchmark
    @OperationsPerInvocation(SearchInputs.BATCH_SIZE)
    public void lowercase(Blackhole blackhole) {
        for (SearchInputs.Input input : inputs) {
            blackhole.consume(CaseInsensitiveSearch.lowercase(input.haystack(), input.needle()));
        }
    }

    @Benchmark
    @OperationsPerInvocation(SearchInputs.BATCH_SIZE)
    public void ascii(Blackhole blackhole) {
        for (SearchInputs.Input input : inputs) {
            blackhole.consume(CaseInsensitiveSearch.ascii(input.haystack(), input.needle()));
        }
    }

    @Benchmark
    @OperationsPerInvocation(SearchInputs.BATCH_SIZE)
    public void regionMatches(Blackhole blackhole) {
        for (SearchInputs.Input input : inputs) {
            blackhole.consume(CaseInsensitiveSearch.regionMatches(input.haystack(), input.needle()));
        }
    }
}
