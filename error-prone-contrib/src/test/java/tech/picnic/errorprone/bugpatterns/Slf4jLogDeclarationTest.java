package tech.picnic.errorprone.bugpatterns;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

final class Slf4jLogDeclarationTest {
  @Test
  void identification() {
    CompilationTestHelper.newInstance(Slf4jLogDeclaration.class, getClass())
        .addSourceLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "// BUG: Diagnostic contains: SLF4J",
            "class A {",
            "  Logger FOO = LoggerFactory.getLogger(A.class);",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  static class B {",
            "    private Logger BAR = LoggerFactory.getLogger(B.class);",
            "  }",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  static class C {",
            "    private static Logger BAZ = LoggerFactory.getLogger(C.class);",
            "  }",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  static class D {",
            "    static final Logger QUX = LoggerFactory.getLogger(D.class);",
            "  }",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  static class E {",
            "    private final Logger QUUX = LoggerFactory.getLogger(E.class);",
            "  }",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  static class F {",
            "    private static final Logger CORGE = LoggerFactory.getLogger(F.class);",
            "  }",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  static class G {",
            "    private static final Logger GRAPLY = LoggerFactory.getLogger(G.class);",
            "  }",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  static class H {",
            "    private static final Logger WALDO = LoggerFactory.getLogger(J.class);",
            "  }",
            "",
            "  class J {}",
            "",
            "  // BUG: Diagnostic contains: SLF4J",
            "  interface K {",
            "    Logger FRED = LoggerFactory.getLogger(A.class);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  void replacementWithDefaultCanonicalizedLoggerName() {
    BugCheckerRefactoringTestHelper.newInstance(Slf4jLogDeclaration.class, getClass())
        .addInputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "class A {",
            "  Logger FOO = LoggerFactory.getLogger(A.class);",
            "}")
        .addOutputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "class A {",
            "  private static final Logger LOG = LoggerFactory.getLogger(A.class);",
            "}")
        .doTest();
  }

  @Test
  void replacementWithOverriddenCanonicalizedLoggerName() {
    BugCheckerRefactoringTestHelper.newInstance(Slf4jLogDeclaration.class, getClass())
        .setArgs(ImmutableList.of("-XepOpt:Slf4jLogDeclaration:CanonicalizedLoggerName=BAR"))
        .addInputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "class A {",
            "  Logger FOO = LoggerFactory.getLogger(A.class);",
            "}")
        .addOutputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "class A {",
            "  private static final Logger BAR = LoggerFactory.getLogger(A.class);",
            "}")
        .doTest();
  }
}
