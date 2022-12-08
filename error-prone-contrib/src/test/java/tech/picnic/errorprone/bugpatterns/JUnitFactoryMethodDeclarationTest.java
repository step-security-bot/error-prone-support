package tech.picnic.errorprone.bugpatterns;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.BugCheckerRefactoringTestHelper.TestMode;
import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

final class JUnitFactoryMethodDeclarationTest {
  private final CompilationTestHelper compilationTestHelper =
      CompilationTestHelper.newInstance(JUnitFactoryMethodDeclaration.class, getClass());
  private final BugCheckerRefactoringTestHelper refactoringTestHelper =
      BugCheckerRefactoringTestHelper.newInstance(JUnitFactoryMethodDeclaration.class, getClass());

  @Test
  void identification() {
    compilationTestHelper
        .addSourceLines(
            "A.java",
            "import static org.junit.jupiter.params.provider.Arguments.arguments;",
            "",
            "import java.util.List;",
            "import java.util.stream.Stream;",
            "import org.junit.jupiter.params.ParameterizedTest;",
            "import org.junit.jupiter.params.provider.Arguments;",
            "import org.junit.jupiter.params.provider.MethodSource;",
            "",
            "class A {",
            "  @ParameterizedTest",
            "  // BUG: Diagnostic contains: The test cases should be supplied by a method named",
            "  // `method1TestCases`",
            "  @MethodSource(\"testCasesForMethod1\")",
            "  void method1(int foo, boolean bar, String baz) {}",
            "",
            "  // BUG: Diagnostic contains: The test cases should be supplied by a method named",
            "  // `method1TestCases`",
            "  private static Stream<Arguments> testCasesForMethod1() {",
            "    /* { foo, bar, baz } */",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "",
            "  @ParameterizedTest",
            "  @MethodSource(\"method2TestCases\")",
            "  void method2(int foo, boolean bar, String baz) {}",
            "",
            "  private static Stream<Arguments> method2TestCases() {",
            "    /* { foo, bar, baz } */",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "",
            "  private static void method2TestCases(int i) {}",
            "",
            "  @ParameterizedTest",
            "  @MethodSource(\"method3TestCases\")",
            "  void method3(int foo, boolean bar, String baz) {}",
            "",
            "  private static Stream<Arguments> method3TestCases() {",
            "    // BUG: Diagnostic contains: The return statement should be prefixed by a comment giving the",
            "    // names of the test case parameters",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "",
            "  @ParameterizedTest",
            "  @MethodSource(\"method4TestCases\")",
            "  void method4(int foo, boolean bar, String baz) {}",
            "",
            "  private static Stream<Arguments> method4TestCases() {",
            "    /* { foo, bar, baz } */",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "",
            "  @ParameterizedTest",
            "  @MethodSource(\"testCasesForMethod5\")",
            "  void method5(int foo, boolean bar, String baz) {}",
            "",
            "  void method5TestCases() {}",
            "",
            "  // BUG: Diagnostic contains: (but note that a method named `method5TestCases` already exists in",
            "  // this class)",
            "  private static Stream<Arguments> testCasesForMethod5() {",
            "    /* { foo, bar, baz } */",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "",
            "  @ParameterizedTest",
            "  @MethodSource(\"method6TestCases\")",
            "  void method6(int foo, boolean bar, String baz) {}",
            "",
            "  private static Stream<Arguments> method6TestCases() {",
            "    List<Arguments> arguments = List.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "    /* { foo, bar, baz } */",
            "    return arguments.stream();",
            "  }",
            "",
            "  @ParameterizedTest",
            "  @MethodSource(\"method7TestCases\")",
            "  void method7(int foo, boolean bar, String baz) {}",
            "",
            "  private static Stream<Arguments> method7TestCases() {",
            "    List<Arguments> arguments = List.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "    // BUG: Diagnostic contains: The return statement should be prefixed by a comment giving the",
            "    // names of the test case parameters",
            "    return arguments.stream();",
            "  }",
            "",
            "  private static Stream<Arguments> method8TestCases() {",
            "    /* { foo, bar, baz } */",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "",
            "  @ParameterizedTest",
            "  @MethodSource(\"method8TestCases\")",
            "  void method8(int foo, boolean bar, String baz) {}",
            "}")
        .doTest();
  }

  @Test
  void replacement() {
    refactoringTestHelper
        .addInputLines(
            "A.java",
            "import static org.junit.jupiter.params.provider.Arguments.arguments;",
            "",
            "import java.util.stream.Stream;",
            "import org.junit.jupiter.params.ParameterizedTest;",
            "import org.junit.jupiter.params.provider.Arguments;",
            "import org.junit.jupiter.params.provider.MethodSource;",
            "",
            "class A {",
            "  @ParameterizedTest",
            "  @MethodSource(\"testCasesForMethod1\")",
            "  void method1(int foo, boolean bar, String baz) {}",
            "",
            "  private static Stream<Arguments> testCasesForMethod1() {",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "}")
        .addOutputLines(
            "A.java",
            "import static org.junit.jupiter.params.provider.Arguments.arguments;",
            "",
            "import java.util.stream.Stream;",
            "import org.junit.jupiter.params.ParameterizedTest;",
            "import org.junit.jupiter.params.provider.Arguments;",
            "import org.junit.jupiter.params.provider.MethodSource;",
            "",
            "class A {",
            "  @ParameterizedTest",
            "  @MethodSource(\"method1TestCases\")",
            "  void method1(int foo, boolean bar, String baz) {}",
            "",
            "  private static Stream<Arguments> method1TestCases() {",
            "    /* { foo, bar, baz } */",
            "    return Stream.of(arguments(1, true, \"A\"), arguments(2, false, \"B\"));",
            "  }",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }
}
