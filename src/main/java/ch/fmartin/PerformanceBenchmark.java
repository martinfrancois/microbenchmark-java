package ch.fmartin;

import ch.fmartin.cut.CharUtils;
import ch.fmartin.cut.RegexUtils;
import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 100, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class PerformanceBenchmark {

  @State(Scope.Thread)
  public static class ThreadState {
    private static final String CHARACTERS_TO_KEEP = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String CHARACTERS_TO_REPLACE = "/\\<>:\"|?*\u007F \t\n\r";
    private static final String REPLACEMENT = "_";
    private static final String EMPTY = "";

    @Param({"20", "2000"})
    int length;

    @Param({"2", "20"})
    int replacementCharRatio; // one non-alpha char per this many chars

    Random random;

    public String testString;
    public String replacementString;

    @Setup(Level.Invocation) // Setup a new string before each invocation
    public void setup() {
      random = new Random(0xdeadbeef); // fix the seed so results are comparable across runs

      int nonAlpha = length / replacementCharRatio;
      int alpha = length - nonAlpha;

      List<Character> chars = Lists.newArrayListWithCapacity(length);
      for (int i = 0; i < alpha; i++) {
        chars.add(randomKeepCharacter());
      }
      for (int i = 0; i < nonAlpha; i++) {
        chars.add(randomReplacementCharacter());
      }
      Collections.shuffle(chars, random);
      char[] array = Chars.toArray(chars);
      testString = new String(array);
      replacementString = randomBoolean() ? REPLACEMENT : EMPTY;
    }

    private char randomKeepCharacter() {
      return CHARACTERS_TO_KEEP.charAt(random.nextInt(CHARACTERS_TO_KEEP.length()));
    }

    private char randomReplacementCharacter() {
      return CHARACTERS_TO_REPLACE.charAt(random.nextInt(CHARACTERS_TO_REPLACE.length()));
    }

    private boolean randomBoolean() {
      return random.nextBoolean();
    }
  }

  @Benchmark
  public void withIteration(ThreadState state, Blackhole bh) {
    bh.consume(CharUtils.removeIllegalCharacters(state.testString, state.replacementString));
  }

  @Benchmark
  public void withRegex(ThreadState state, Blackhole bh) {
    bh.consume(RegexUtils.removeIllegalCharacters(state.testString, state.replacementString));
  }
}
