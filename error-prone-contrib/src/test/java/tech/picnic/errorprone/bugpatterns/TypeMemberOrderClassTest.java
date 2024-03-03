package tech.picnic.errorprone.bugpatterns;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.BugCheckerRefactoringTestHelper.TestMode;
import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.Test;

final class TypeMemberOrderClassTest {
  @Test
  void identification() {
    CompilationTestHelper.newInstance(TypeMemberOrder.class, getClass())
        .expectErrorMessage(
            "TypeMemberOrder",
            message -> message.contains("Type members should be ordered in a standard way"))
        .addSourceLines(
            "A.java",
            "// BUG: Diagnostic matches: TypeMemberOrder",
            "class A {",
            "  class InnerClass {}",
            "",
            "  interface InnerInterface {}",
            "",
            "  enum InnerEnum {}",
            "",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  A() {}",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  private final int bar = 2;",
            "  private static final int foo = 1;",
            "}")
        .addSourceLines(
            "B.java",
            "class B {",
            "  private static final int foo = 1;",
            "  private final int bar = 2;",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  B() {}",
            "",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  class InnerClass {}",
            "",
            "  interface InnerInterface {}",
            "",
            "  enum InnerEnum {}",
            "}")
        .addSourceLines(
            "C.java",
            "class C {",
            "  @SuppressWarnings({\"foo\", \"all\", \"bar\"})",
            "  void unorderedMethod() {}",
            "",
            "  C() {}",
            "}")
        .addSourceLines(
            "D.java",
            "class D {",
            "  @SuppressWarnings(\"TypeMemberOrder\")",
            "  void unorderedMethod() {}",
            "",
            "  D() {}",
            "}")
        .addSourceLines("E.java", "class E {}")
        .doTest();
  }

  @Test
  void replacementSuggestedFix() {
    BugCheckerRefactoringTestHelper.newInstance(TypeMemberOrder.class, getClass())
        .addInputLines(
            "A.java",
            "class A {",
            "  class Inner {}",
            "",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  A() {}",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  private final int bar = 2;",
            "  private static final int foo = 1;",
            "}")
        .addOutputLines(
            "A.java",
            "class A {",
            "  private static final int foo = 1;",
            "",
            "  private final int bar = 2;",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  A() {}",
            "",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  class Inner {}",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }

  @Test
  void replacementSuggestedFixAbstractMethods() {
    BugCheckerRefactoringTestHelper.newInstance(TypeMemberOrder.class, getClass())
        .addInputLines(
            "A.java",
            "abstract class A {",
            "  static class InnerClass {}",
            "",
            "  void foo() {}",
            "",
            "  abstract void bar();",
            "",
            "  void baz() {}",
            "",
            "  A() {}",
            "}")
        .addOutputLines(
            "A.java",
            "abstract class A {",
            "",
            "  A() {}",
            "",
            "  void foo() {}",
            "",
            "  abstract void bar();",
            "",
            "  void baz() {}",
            "",
            "  static class InnerClass {}",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }

  @Test
  void replacementSuggestedFixConsidersUnmovableMembers() {
    BugCheckerRefactoringTestHelper.newInstance(TypeMemberOrder.class, getClass())
        .addInputLines(
            "A.java",
            "class A {",
            "  @SuppressWarnings(\"TypeMemberOrder\")",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  private final int bar = 2;",
            "",
            "  @SuppressWarnings(\"TypeMemberOrder\")",
            "  private static final int foo = 1;",
            "}")
        .addOutputLines(
            "A.java",
            "class A {",
            "  @SuppressWarnings(\"TypeMemberOrder\")",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  private final int bar = 2;",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  @SuppressWarnings(\"TypeMemberOrder\")",
            "  private static final int foo = 1;",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }

  @Test
  void replacementSuggestedFixConsidersDefaultConstructor() {
    BugCheckerRefactoringTestHelper.newInstance(TypeMemberOrder.class, getClass())
        .addInputLines(
            "A.java",
            "class A {",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  private final int bar = 2;",
            "  private static final int foo = 1;",
            "}")
        .addOutputLines(
            "A.java",
            "class A {",
            "  private static final int foo = 1;",
            "",
            "  private final int bar = 2;",
            "",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  int foo() {",
            "    return foo;",
            "  }",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }

  @Test
  void replacementSuggestedFixConsidersDanglingComments() {
    BugCheckerRefactoringTestHelper.newInstance(TypeMemberOrder.class, getClass())
        .addInputLines(
            "A.java",
            "class A {",
            "  /* `foo` method's dangling comment. */",
            "  ;",
            "  // `foo` method's comment",
            "  int foo() {",
            "    return foo;",
            "  }",
            "",
            "  // initializer block's comment",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "",
            "  // static initializer block's comment",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "  /* `bar` field's dangling comment. */ ;",
            "",
            "  private final int bar = 2;",
            "  // `foo` field's comment",
            "  private static final int foo = 1;",
            "  // trailing comment",
            "}")
        .addOutputLines(
            "A.java",
            "class A {",
            "  // `foo` field's comment",
            "  private static final int foo = 1;",
            "  /* `bar` field's dangling comment. */ ;",
            "",
            "  private final int bar = 2;",
            "",
            "  // static initializer block's comment",
            "  static {",
            "    System.out.println(\"foo\");",
            "  }",
            "",
            "  // initializer block's comment",
            "  {",
            "    System.out.println(\"bar\");",
            "  }",
            "  /* `foo` method's dangling comment. */",
            "  ;",
            "",
            "  // `foo` method's comment",
            "  int foo() {",
            "    return foo;",
            "  }",
            "  // trailing comment",
            "}")
        .doTest(TestMode.TEXT_MATCH);
  }
}
