package tech.picnic.errorprone.bugpatterns;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.BugCheckerRefactoringTestHelper.TestMode;
import org.junit.jupiter.api.Test;

final class Slf4jLogDeclarationTest {
  @Test
  void replacement() {
    BugCheckerRefactoringTestHelper.newInstance(Slf4jLogDeclaration.class, getClass())
        .addInputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "class A {",
            "  Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(A.class);",
            "",
            "  static class B {",
            "    private Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(B.class);",
            "  }",
            "",
            "  static class C {",
            "    private static Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(C.class);",
            "  }",
            "",
            "  static class D {",
            "    static final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(D.class);",
            "  }",
            "",
            "  static class E {",
            "    private final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(E.class);",
            "  }",
            "",
            "  static class F {",
            "    private static final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(F.class);",
            "  }",
            "",
            "  static class G {",
            "    private static final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(G.class);",
            "  }",
            "",
            "  static class H {",
            "    private static final Logger LOGGER_WITH_WRONG_CLASS_AS_ARGUMENT =",
            "        LoggerFactory.getLogger(J.class);",
            "  }",
            "",
            "  class J {}",
            "",
            "  interface K {",
            "    Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(A.class);",
            "  }",
            "}")
        .addOutputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "class A {",
            "  private static final Logger LOG = LoggerFactory.getLogger(A.class);",
            "",
            "  static class B {",
            "    private static final Logger LOG = LoggerFactory.getLogger(B.class);",
            "  }",
            "",
            "  static class C {",
            "    private static final Logger LOG = LoggerFactory.getLogger(C.class);",
            "  }",
            "",
            "  static class D {",
            "    private static final Logger LOG = LoggerFactory.getLogger(D.class);",
            "  }",
            "",
            "  static class E {",
            "    private static final Logger LOG = LoggerFactory.getLogger(E.class);",
            "  }",
            "",
            "  static class F {",
            "    private static final Logger LOG = LoggerFactory.getLogger(F.class);",
            "  }",
            "",
            "  static class G {",
            "    private static final Logger LOG = LoggerFactory.getLogger(G.class);",
            "  }",
            "",
            "  static class H {",
            "    private static final Logger LOG = LoggerFactory.getLogger(H.class);",
            "  }",
            "",
            "  class J {}",
            "",
            "  interface K {",
            "    Logger LOG = LoggerFactory.getLogger(K.class);",
            "  }",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }
}