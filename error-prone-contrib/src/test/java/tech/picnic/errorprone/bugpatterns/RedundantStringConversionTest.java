package tech.picnic.errorprone.bugpatterns;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.BugCheckerRefactoringTestHelper.TestMode;
import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

// XXX: The tests below show that `String.valueOf((String) null)` may be simplified, but
// `String.valueOf(null)` may not. That is because the latter matches `String#valueOf(char[])`. We
// could special-case `null` arguments, but that doesn't seem worth the trouble.
final class RedundantStringConversionTest {
  @Test
  void identificationOfIdentityTransformation() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addSourceLines(
            "A.java",
            """
            class A {
              private final Object o = new Object();
              private final String s = o.toString();

              String[] m() {
                return new String[] {
                  o.toString(),
                  // BUG: Diagnostic contains:
                  s.toString(),
                  String.valueOf(o),
                  // BUG: Diagnostic contains:
                  String.valueOf(s),
                };
              }
            }
            """)
        .doTest();
  }

  @Test
  void identificationWithinMutatingAssignment() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addSourceLines(
            "A.java",
            """
            import java.math.BigInteger;
            import java.util.Objects;

            class A {
              private final BigInteger i = BigInteger.ZERO;

              void m1() {
                String s = i.toString();
                // BUG: Diagnostic contains:
                s += this.toString();
                s += super.toString();
                // BUG: Diagnostic contains:
                s += i.toString();
                s += i.toString(16);
                // BUG: Diagnostic contains:
                s += Objects.toString(i);
                // BUG: Diagnostic contains:
                s += Objects.toString(null);
                // BUG: Diagnostic contains:
                s += String.valueOf(i);
                // BUG: Diagnostic contains:
                s += String.valueOf(0);
                // BUG: Diagnostic contains:
                s += String.valueOf((String) null);
                s += String.valueOf(null);
                s += String.valueOf(new char[0]);
                s += String.valueOf(new char[0], 0, 0);
                // BUG: Diagnostic contains:
                s += Boolean.toString(false);
                // BUG: Diagnostic contains:
                s += Byte.toString((byte) 0);
                // BUG: Diagnostic contains:
                s += Character.toString((char) 0);
                // BUG: Diagnostic contains:
                s += Short.toString((short) 0);
                // BUG: Diagnostic contains:
                s += Integer.toString(0);
                // BUG: Diagnostic contains:
                s += Long.toString(0);
                // BUG: Diagnostic contains:
                s += Float.toString((float) 0.0);
                // BUG: Diagnostic contains:
                s += Double.toString(0.0);
              }

              void m2() {
                int i = 0;
                i += 1;
                i -= 1;
                i *= 1;
                i /= 1;
              }
            }
            """)
        .doTest();
  }

  @Test
  void identificationWithinBinaryOperation() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addSourceLines(
            "A.java",
            """
            import java.math.BigInteger;

            class A {
              private final BigInteger i = BigInteger.ZERO;
              private final String s = i.toString();

              String[] m1() {
                return new String[] {
                  // BUG: Diagnostic contains:
                  s + this.toString(),
                  s + super.toString(),
                  // BUG: Diagnostic contains:
                  s + i.toString(),
                  s + i.toString(16),
                  // BUG: Diagnostic contains:
                  s + String.valueOf(i),
                  // BUG: Diagnostic contains:
                  s + String.valueOf(0),
                  // BUG: Diagnostic contains:
                  s + String.valueOf((String) null),
                  s + String.valueOf(null),
                  s + String.valueOf(new char[0]),
                  s + String.valueOf(new char[0], 0, 0),
                  //
                  42 + this.toString(),
                  42 + super.toString(),
                  42 + i.toString(),
                  42 + i.toString(16),
                  42 + String.valueOf(i),
                  // BUG: Diagnostic contains:
                  42 + String.valueOf((String) null),
                  42 + String.valueOf(null),
                  42 + String.valueOf(new char[0]),
                  42 + String.valueOf(new char[0], 0, 0),

                  // BUG: Diagnostic contains:
                  this.toString() + s,
                  super.toString() + s,
                  // BUG: Diagnostic contains:
                  i.toString() + s,
                  i.toString(16) + s,
                  // BUG: Diagnostic contains:
                  String.valueOf(i) + s,
                  // BUG: Diagnostic contains:
                  String.valueOf(0) + s,
                  // BUG: Diagnostic contains:
                  String.valueOf((String) null) + s,
                  String.valueOf(null) + s,
                  String.valueOf(new char[0]) + s,
                  String.valueOf(new char[0], 0, 0) + s,
                  //
                  this.toString() + 42,
                  super.toString() + 42,
                  i.toString() + 42,
                  i.toString(16) + 42,
                  String.valueOf(i) + 42,
                  String.valueOf(0) + 42,
                  // BUG: Diagnostic contains:
                  String.valueOf((String) null) + 42,
                  String.valueOf(null) + 42,
                  String.valueOf(new char[0]) + 42,
                  String.valueOf(new char[0], 0, 0) + 42,

                  // BUG: Diagnostic contains:
                  this.toString() + this.toString(),
                  super.toString() + super.toString(),
                  // BUG: Diagnostic contains:
                  i.toString() + i.toString(),
                  i.toString(16) + i.toString(16),
                  // BUG: Diagnostic contains:
                  String.valueOf(i) + String.valueOf(i),
                  // BUG: Diagnostic contains:
                  String.valueOf(0) + String.valueOf(0),
                  // BUG: Diagnostic contains:
                  String.valueOf((String) null) + String.valueOf((String) null),
                  String.valueOf(null) + String.valueOf(null),
                  String.valueOf(new char[0]) + String.valueOf(new char[0]),
                  String.valueOf(new char[0], 0, 0) + String.valueOf(new char[0], 0, 0),
                };
              }

              int[] m2() {
                return new int[] {
                  1 + 1, 1 - 1, 1 * 1, 1 / 1,
                };
              }
            }
            """)
        .doTest();
  }

  @Test
  void identificationWithinStringBuilderMethod() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addSourceLines(
            "A.java",
            """
            import java.math.BigInteger;

            class A {
              private final BigInteger i = BigInteger.ZERO;
              private final String s = i.toString();

              void m() {
                StringBuilder sb = new StringBuilder();

                sb.append(1);
                sb.append(i);
                // BUG: Diagnostic contains:
                sb.append(i.toString());
                sb.append(i.toString(16));
                // BUG: Diagnostic contains:
                sb.append(String.valueOf(i));
                // BUG: Diagnostic contains:
                sb.append(String.valueOf(0));
                // BUG: Diagnostic contains:
                sb.append(String.valueOf((String) null));
                sb.append(String.valueOf(null));
                sb.append(String.valueOf(new char[0]));
                sb.append(String.valueOf(new char[0], 0, 0));
                sb.append(s);
                sb.append("constant");

                sb.insert(0, 1);
                sb.insert(0, i);
                // BUG: Diagnostic contains:
                sb.insert(0, i.toString());
                sb.insert(0, i.toString(16));
                // BUG: Diagnostic contains:
                sb.insert(0, String.valueOf(i));
                // BUG: Diagnostic contains:
                sb.insert(0, String.valueOf(0));
                // BUG: Diagnostic contains:
                sb.insert(0, String.valueOf((String) null));
                sb.insert(0, String.valueOf(null));
                sb.insert(0, String.valueOf(new char[0]));
                sb.insert(0, String.valueOf(new char[0], 0, 0));
                sb.insert(0, s);
                sb.insert(0, "constant");

                sb.replace(0, 1, i.toString());
              }
            }
            """)
        .doTest();
  }

  // XXX: Also test the other formatter methods.
  @Test
  void identificationWithinFormatterMethod() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addSourceLines(
            "A.java",
            """
            import java.util.Formattable;
            import java.util.Locale;

            class A {
              private final Locale locale = Locale.ROOT;
              private final Formattable f = (formatter, flags, width, precision) -> {};
              private final Object o = new Object();
              private final String s = o.toString();

              void m() {
                String.format(s, f);
                String.format(s, o);
                String.format(s, s);
                String.format(s, f.toString());
                // BUG: Diagnostic contains:
                String.format(s, o.toString());
                // BUG: Diagnostic contains:
                String.format(s, String.valueOf(o));

                String.format(locale, s, f);
                String.format(locale, s, o);
                String.format(locale, s, s);
                String.format(locale, s, f.toString());
                // BUG: Diagnostic contains:
                String.format(locale, s, o.toString());
                // BUG: Diagnostic contains:
                String.format(locale, s, String.valueOf(o));

                String.format(o.toString(), o);
                // BUG: Diagnostic contains:
                String.format(s.toString(), o);
                String.format(locale.toString(), s, o);
                String.format(locale, o.toString(), o);
                // BUG: Diagnostic contains:
                String.format(locale, s.toString(), o);
              }
            }
            """)
        .doTest();
  }

  @Test
  void identificationWithinGuavaGuardMethod() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addSourceLines(
            "A.java",
            """
            import static com.google.common.base.Preconditions.checkArgument;
            import static com.google.common.base.Preconditions.checkNotNull;
            import static com.google.common.base.Preconditions.checkState;
            import static com.google.common.base.Verify.verify;
            import static com.google.common.base.Verify.verifyNotNull;

            import java.util.Formattable;

            class A {
              private final Formattable f = (formatter, flags, width, precision) -> {};
              private final Object o = new Object();
              private final String s = o.toString();

              void m() {
                checkState(true, s, f);
                // BUG: Diagnostic contains:
                checkState(true, s, f.toString());
                checkState(true, f.toString(), f);
                // BUG: Diagnostic contains:
                checkState(true, s.toString(), f);

                checkArgument(true, s, f);
                // BUG: Diagnostic contains:
                checkArgument(true, s, f.toString());
                checkArgument(true, f.toString(), f);
                // BUG: Diagnostic contains:
                checkArgument(true, s.toString(), f);

                checkNotNull(o, s, f);
                // BUG: Diagnostic contains:
                checkNotNull(o, s, f.toString());
                checkNotNull(o, f.toString(), f);
                // BUG: Diagnostic contains:
                checkNotNull(o, s.toString(), f);
                checkNotNull(o.toString(), s, f);

                verify(true, s, f);
                // BUG: Diagnostic contains:
                verify(true, s, f.toString());
                verify(true, f.toString(), f);
                // BUG: Diagnostic contains:
                verify(true, s.toString(), f);

                verifyNotNull(o, s, f);
                // BUG: Diagnostic contains:
                verifyNotNull(o, s, f.toString());
                verifyNotNull(o, f.toString(), f);
                // BUG: Diagnostic contains:
                verifyNotNull(o, s.toString(), f);
                verifyNotNull(o.toString(), s, f);
              }
            }
            """)
        .doTest();
  }

  @Test
  void identificationWithinSlf4jLoggerMethod() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addSourceLines(
            "A.java",
            """
            import java.util.Formattable;
            import org.slf4j.Logger;
            import org.slf4j.LoggerFactory;
            import org.slf4j.Marker;
            import org.slf4j.MarkerFactory;

            class A {
              private static final Logger LOG = LoggerFactory.getLogger(A.class);

              private final Marker marker = MarkerFactory.getMarker(A.class.getName());
              private final Formattable f = (formatter, flags, width, precision) -> {};
              private final Object o = new Object();
              private final String s = f.toString();
              private final Throwable t = new Throwable();

              void m() {
                LOG.trace(s, f);
                // BUG: Diagnostic contains:
                LOG.debug(s, f.toString());
                LOG.info(s, t.toString());
                LOG.warn(s, o, t.toString());
                // BUG: Diagnostic contains:
                LOG.error(s, o.toString(), t.toString());
                // BUG: Diagnostic contains:
                LOG.trace(s, t.toString(), o);

                LOG.trace(marker, s, f);
                // BUG: Diagnostic contains:
                LOG.debug(marker, s, f.toString());
                LOG.info(marker, s, t.toString());
                LOG.warn(marker, s, o, t.toString());
                // BUG: Diagnostic contains:
                LOG.error(marker, s, o.toString(), t.toString());
                // BUG: Diagnostic contains:
                LOG.trace(marker, s, t.toString(), o);

                LOG.trace(f.toString(), f);
                // BUG: Diagnostic contains:
                LOG.debug(s.toString(), f);
                LOG.info(t.toString(), f);
                LOG.warn(marker.toString(), s, f);
                LOG.error(marker, o.toString(), f);
                // BUG: Diagnostic contains:
                LOG.trace(marker, s.toString(), f);
                LOG.debug(marker, t.toString(), f);
              }
            }
            """)
        .doTest();
  }

  @Test
  void identificationOfCustomConversionMethod() {
    CompilationTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .setArgs(
            ImmutableList.of(
                "-XepOpt:RedundantStringConversion:ExtraConversionMethods=java.lang.Enum#name(),A#name(),A.B#toString(int)"))
        .addSourceLines(
            "A.java",
            """
            import java.math.RoundingMode;
            import java.util.Objects;

            class A {
              static class B {
                String name() {
                  return toString();
                }

                static String toString(int i) {
                  return Integer.toString(i);
                }

                static String toString(int i, int j) {
                  return Integer.toString(i * j);
                }
              }

              enum E {
                ELEM;

                public String toString() {
                  return "__" + name() + "__";
                }
              }

              private final B b = new B();
              private final String s = b.toString();

              String[] builtin() {
                return new String[] {
                  // BUG: Diagnostic contains:
                  s + b.toString(),
                  // BUG: Diagnostic contains:
                  s + Objects.toString(b),
                  // BUG: Diagnostic contains:
                  s + String.valueOf(b),
                  // BUG: Diagnostic contains:
                  s + Boolean.toString(false),
                  // BUG: Diagnostic contains:
                  s + Byte.toString((byte) 0),
                  // BUG: Diagnostic contains:
                  s + Character.toString((char) 0),
                  // BUG: Diagnostic contains:
                  s + Short.toString((short) 0),
                  // BUG: Diagnostic contains:
                  s + Integer.toString(0),
                  s + Integer.toString(0, 16),
                  // BUG: Diagnostic contains:
                  s + Long.toString(0),
                  s + Long.toString(0, 16),
                  // BUG: Diagnostic contains:
                  s + Float.toString((float) 0.0),
                  // BUG: Diagnostic contains:
                  s + Double.toString(0.0),
                };
              }

              String[] custom() {
                return new String[] {
                  s + b.name(),
                  // BUG: Diagnostic contains:
                  s + RoundingMode.UP.name(),
                  // BUG: Diagnostic contains:
                  s + mode().name(),
                  s + A.name(),
                  s + A.toString(42),
                  // BUG: Diagnostic contains:
                  s + B.toString(42),
                  s + B.toString(42, 42),
                };
              }

              static String name() {
                return A.class.toString();
              }

              RoundingMode mode() {
                return RoundingMode.UP;
              }

              static String toString(int i) {
                return Integer.toString(i);
              }
            }
            """)
        .doTest();
  }

  @Test
  void replacement() {
    BugCheckerRefactoringTestHelper.newInstance(RedundantStringConversion.class, getClass())
        .addInputLines(
            "A.java",
            """
            class A {
              private final Object o = new Object();
              private final String s = o.toString();

              void m() {
                String v1 = s.toString();
                String v2 = "foo".toString();
                String v3 = v2 + super.toString();
                String v4 = 42 + String.valueOf((String) null);
                String.format("%s", o.toString());
              }
            }
            """)
        .addOutputLines(
            "A.java",
            """
            class A {
              private final Object o = new Object();
              private final String s = o.toString();

              void m() {
                String v1 = s;
                String v2 = "foo";
                String v3 = v2 + super.toString();
                String v4 = 42 + (String) null;
                String.format("%s", o);
              }
            }
            """)
        .doTest(TestMode.TEXT_MATCH);
  }
}
