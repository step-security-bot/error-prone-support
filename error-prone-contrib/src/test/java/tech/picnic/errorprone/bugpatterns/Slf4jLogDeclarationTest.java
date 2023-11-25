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
            "  class B {",
            "    private Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(B.class);",
            "  }",
            "",
            "  class C {",
            "    private static Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(C.class);",
            "  }",
            "",
            "  class D {",
            "    static final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(D.class);",
            "  }",
            "",
            "  class E {",
            "    private final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(E.class);",
            "  }",
            "",
            "  class F {",
            "    private static final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(F.class);",
            "  }",
            "",
            "  class G {",
            "    private static final Logger NOT_PROPER_LOGGER_NAME = LoggerFactory.getLogger(G.class);",
            "  }",
            "",
            "  class H {",
            "    private static final Logger LOGGER_WITH_WRONG_CLASS_AS_ARGUMENT =",
            "      LoggerFactory.getLogger(J.class);",
            "  }",
            "",
            "  class J {}",
            "}")
        .addOutputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "class A {",
            "  private static final Logger LOG = LoggerFactory.getLogger(A.class);",
            "",
            "  class B {",
            "    private static final  Logger LOG = LoggerFactory.getLogger(B.class);",
            "  }",
            "",
            "  class C {",
            "    private static final Logger LOG = LoggerFactory.getLogger(C.class);",
            "  }",
            "",
            "  class D {",
            "    private static final Logger LOG = LoggerFactory.getLogger(D.class);",
            "  }",
            "",
            "  class E {",
            "    private static final Logger LOG = LoggerFactory.getLogger(E.class);",
            "  }",
            "",
            "  class F {",
            "    private static final Logger LOG = LoggerFactory.getLogger(F.class);",
            "  }",
            "",
            "  class G {",
            "    private static final Logger LOG = LoggerFactory.getLogger(G.class);",
            "  }",
            "",
            "  class H {",
            "    private static final Logger LOG = LoggerFactory.getLogger(H.class);",
            "  }",
            "",
            "  class J {}",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }

  @Test
  void doNotAddModifiersToDeclarationsInsideInterfaces() {
    BugCheckerRefactoringTestHelper.newInstance(Slf4jLogDeclaration.class, getClass())
        .addInputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "interface A {",
            "  Logger LOG = LoggerFactory.getLogger(A.class);",
            "",
            "  Logger LOG1 = LoggerFactory.getLogger(B.class);",
            "",
            "  class B {}",
            "}")
        .addOutputLines(
            "A.java",
            "import org.slf4j.Logger;",
            "import org.slf4j.LoggerFactory;",
            "",
            "interface A {",
            "  Logger LOG = LoggerFactory.getLogger(A.class);",
            "",
            "  Logger LOG1 = LoggerFactory.getLogger(A.class);",
            "",
            "  class B {}",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }
}
