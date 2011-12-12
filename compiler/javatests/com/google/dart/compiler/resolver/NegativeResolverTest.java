// Copyright (c) 2011, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.
package com.google.dart.compiler.resolver;

import com.google.dart.compiler.CompilerTestCase;
import com.google.dart.compiler.DartCompilationError;
import com.google.dart.compiler.ast.DartThisExpression;
import com.google.dart.compiler.ast.DartUnit;
import com.google.dart.compiler.testing.TestCompilerContext;

import java.util.ArrayList;
import java.util.List;

// TODO(ngeoffray): Move these tests to the VM tests once we can run VM tests.
public class NegativeResolverTest extends CompilerTestCase {
  List<DartCompilationError> errors = new ArrayList<DartCompilationError>();
  List<DartCompilationError> typeErrors = new ArrayList<DartCompilationError>();

  /**
   * Parses given Dart source, runs {@link Resolver} and checks that expected errors were generated.
   */
  public void checkSourceErrors(String source, ErrorExpectation... expectedErrors) {
    DartUnit unit = parseUnit("Test.dart", source);
    resolve(unit);
    assertErrors(errors, expectedErrors);
  }

  /**
   * Parses given Dart file, runs {@link Resolver} and checks that expected errors were generated.
   */
  public void checkFileErrors(String source, ErrorExpectation... expectedErrors) {
    DartUnit unit = parseUnit(source);
    resolve(unit);
    assertErrors(errors, expectedErrors);
  }

  public void checkNumErrors(String fileName, int expectedErrorCount) {
    DartUnit unit = parseUnit(fileName);
    resolve(unit);
    assertEquals(new ArrayList<DartCompilationError>(), typeErrors);
    if (errors.size() != expectedErrorCount) {
      fail(String.format(
          "Expected %s errors, but got %s: %s",
          expectedErrorCount,
          errors.size(),
          errors));
    }
  }

  private void resolve(DartUnit unit) {
    unit.addTopLevelNode(ResolverTestCase.makeClass("int", null));
    unit.addTopLevelNode(ResolverTestCase.makeClass("Object", null));
    unit.addTopLevelNode(ResolverTestCase.makeClass("String", null));
    unit.addTopLevelNode(ResolverTestCase.makeClass("Function", null));
    unit.addTopLevelNode(ResolverTestCase.makeClass("List", null, "T"));
    unit.addTopLevelNode(ResolverTestCase.makeClass("Map", null, "K", "V"));
    ResolverTestCase.resolve(unit, getContext());
  }

  public void testInitializer1() {
    checkNumErrors("Initializer1NegativeTest.dart", 1);
  }

  public void testInitializer2() {
    checkNumErrors("Initializer2NegativeTest.dart", 1);
  }

  public void testInitializer3() {
    checkNumErrors("Initializer3NegativeTest.dart", 1);
  }

  public void testInitializer4() {
    checkNumErrors("Initializer4NegativeTest.dart", 1);
  }

  public void testInitializer5() {
    checkNumErrors("Initializer5NegativeTest.dart", 1);
  }

  public void testInitializer6() {
    checkNumErrors("Initializer6NegativeTest.dart", 1);
  }

  public void testArrayLiteralNegativeTest() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  main() {",
            "    List<int, int> ints = [1];",
            "  }",
            "}"),
        errEx(TypeErrorCode.WRONG_NUMBER_OF_TYPE_ARGUMENTS, 4, 5, 14));
  }

  public void testMapLiteralNegativeTest() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  main() {",
            "    Map<String, int, int> map = {'foo':1};",
            "  }",
            "}"),
        errEx(TypeErrorCode.WRONG_NUMBER_OF_TYPE_ARGUMENTS, 4, 5, 21));
  }

  /**
   * We should not fail in case of using {@link DartThisExpression} outside of method.
   * <p>
   * http://code.google.com/p/dart/issues/detail?id=662
   */
  public void test_thisExpression_inTopLevelVariable() {
    checkSourceErrors("var foo = this;", errEx(ResolverErrorCode.THIS_ON_TOP_LEVEL, 1, 11, 4));
  }

  public void test_thisExpression_inTopLevelMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "foo() {",
            "  return this;",
            "}"),
        errEx(ResolverErrorCode.THIS_ON_TOP_LEVEL, 3, 10, 4));
  }

  public void test_thisExpression_outsideOfMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  var foo = this;",
            "}"),
        errEx(ResolverErrorCode.THIS_OUTSIDE_OF_METHOD, 3, 13, 4));
  }

  public void test_thisExpression_inStaticMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  static foo() {",
            "    return this;",
            "  }",
            "}"),
        errEx(ResolverErrorCode.THIS_IN_STATIC_METHOD, 4, 12, 4));
  }

  public void test_thisExpression_inFactoryMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  factory A() {",
            "    return this;",
            "  }",
            "}"),
        errEx(ResolverErrorCode.THIS_IN_FACTORY_CONSTRUCTOR, 4, 12, 4));
  }

  /**
   * We should not fail in case of using {@link DartThisExpression} outside of method.
   * <p>
   * http://code.google.com/p/dart/issues/detail?id=662
   */
  public void test_superExpression_inTopLevelVariable() {
    checkSourceErrors(
        "var foo = super.foo();",
        errEx(ResolverErrorCode.SUPER_ON_TOP_LEVEL, 1, 11, 5));
  }

  public void test_superExpression_inTopLevelMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "foo() {",
            "  return super.foo();",
            "}"),
        errEx(ResolverErrorCode.SUPER_ON_TOP_LEVEL, 3, 10, 5));
  }

  public void test_superExpression_outsideOfMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  var foo = super.foo();",
            "}"),
        errEx(ResolverErrorCode.SUPER_OUTSIDE_OF_METHOD, 3, 13, 5));
  }

  public void test_superExpression_inStaticMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  static foo() {",
            "    return super.foo();",
            "  }",
            "}"),
        errEx(ResolverErrorCode.SUPER_IN_STATIC_METHOD, 4, 12, 5));
  }

  public void test_superExpression_inFactoryMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  factory A() {",
            "    return super.foo();",
            "  }",
            "}"),
        errEx(ResolverErrorCode.SUPER_IN_FACTORY_CONSTRUCTOR, 4, 12, 5));
  }

  public void testNameConflict_field_field() {
    checkSourceErrors(
        makeCode(
            "class ClassDeclarationWithLongEnoughNameToForceLineSplitting {",
            "  var foo;",
            "  var foo;",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 2, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3));
  }

  public void testCall1() {
    checkNumErrors("StaticInstanceCallNegativeTest.dart", 1);
  }

  public void testClassExtendsInterfaceNegativeTest() {
    checkNumErrors("ClassExtendsInterfaceNegativeTest.dart", 1);
  }

  public void tesClassImplementsUnknownInterfaceNegativeTest() {
    checkNumErrors("ClassImplementsUnknownInterfaceNegativeTest.dart", 1);
  }

  public void testConstSuperNegativeTest1() {
    checkNumErrors("ConstSuperNegativeTest1.dart", 0);
  }

  public void testConstSuperNegativeTest2() {
    checkNumErrors("ConstSuperNegativeTest2.dart", 1);
  }

  public void testConstSuperTest() {
    checkNumErrors("ConstSuperTest.dart", 0);
  }

  public void testParameterInitializerNegativeTest1() {
    checkNumErrors("ParameterInitializerNegativeTest1.dart", 1);
  }

  public void testParameterInitializerNegativeTest2() {
    checkNumErrors("ParameterInitializerNegativeTest2.dart", 1);
  }

  public void testParameterInitializerNegativeTest3() {
    checkNumErrors("ParameterInitializerNegativeTest3.dart", 1);
  }

  public void testStaticToInstanceInvocationNegativeTest1() {
    checkNumErrors("StaticToInstanceInvocationNegativeTest1.dart", 1);
  }

  public void testConstVariableInitializationNegativeTest1() {
    checkNumErrors("ConstVariableInitializationNegativeTest1.dart", 1);
  }

  public void testConstVariableInitializationNegativeTest2() {
    checkNumErrors("ConstVariableInitializationNegativeTest2.dart", 1);
  }

  public void test_nameShadow_topLevel_getterSetter_class() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "get foo() {}",
            "set bar(x) {}",
            "class foo {}",
            "class bar{}"),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 2, 5, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 4, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 3, 5, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 5, 7, 3));
  }

  public void test_nameShadow_topLevel_class_getterSetter() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class foo {}",
            "class bar {}",
            "get foo() {}",
            "set bar(x) {}"),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 2, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 4, 5, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 5, 5, 3));
  }

  public void test_nameShadow_topLevel_getter_setter() {
    checkSourceErrors(makeCode(
        "// filler filler filler filler filler filler filler filler filler filler",
        "get bar() {}",
        "set bar(x) {}"));
  }

  public void test_nameShadow_topLevel_setter_getter() {
    checkSourceErrors(makeCode(
        "// filler filler filler filler filler filler filler filler filler filler",
        "set bar(x) {}",
        "get bar() {}"));
  }

  public void test_nameShadow_topLevel_getters() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "get bar() {}",
            "get bar() {}"),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 2, 5, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 3, 5, 3));
  }

  public void test_nameShadow_topLevel_setters() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "set bar(x) {}",
            "set bar(x) {}"),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 2, 5, 3),
        errEx(ResolverErrorCode.DUPLICATE_TOP_LEVEL_DEFINITION, 3, 5, 3));
  }

  /**
   * Multiple unnamed constructor definitions.
   */
  public void test_nameShadow_unnamedConstructors() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  A(x) {}",
            "  A(x,y) {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 3, 1),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 3, 1));
    {
      String message = errors.get(0).getMessage();
      assertTrue(message, message.contains("'A'"));
    }
    {
      String message = errors.get(1).getMessage();
      assertTrue(message, message.contains("'A'"));
    }
  }

  /**
   * Multiple unnamed constructor definitions. Make sure modifiers works as expected.
   */
  public void test_nameShadow_unnamedConstructors_constModifier() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  A(x) {}",
            "  const A(x,y) {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 3, 1),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 9, 1),
        errEx(ResolverErrorCode.CONST_CONSTRUCTOR_CANNOT_HAVE_BODY, 4, 9, 1));
    {
      String message = errors.get(0).getMessage();
      assertTrue(message, message.contains("'A'"));
    }
  }

  /**
   * Named constructor shadows another named constructor.
   */
  public void test_nameShadow_namedConstructors() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  A.foo() {}",
            "  A.foo() {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 3, 5),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 3, 5));
    {
      String message = errors.get(0).getMessage();
      assertTrue(message, message.contains("'A.foo'"));
    }
    {
      String message = errors.get(1).getMessage();
      assertTrue(message, message.contains("'A.foo'"));
    }
  }

  /**
   * Method shadows another method.
   */
  public void test_nameShadow_methods() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  foo() {}",
            "  foo() {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 3, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 3, 3));
  }

  /**
   * Field shadows method.
   */
  public void test_nameShadow_method_field() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  foo() {}",
            "  var foo;",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 3, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 7, 3));
  }

  /**
   * Static method shadows instance method.
   */
  public void test_nameShadow_method_staticMethod() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  foo(x) {}",
            "  static foo(a,b) {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 3, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 10, 3));
  }

  /**
   * Field shadows another field.
   */
  public void test_nameShadow_fields() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  var _a;",
            "  var _a;",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 2),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 7, 2));
  }

  /**
   * Field shadows setter/getter.
   */
  public void test_nameShadow_setter_field() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  set foo(x) {}",
            "  get foo() {}",
            "  var foo;",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 5, 7, 3));
  }

  /**
   * Setter shadows field.
   */
  public void test_nameShadow_field_setter() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  var foo;",
            "  set foo(x) {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 7, 3));
  }

  /**
   * Method shadows setter.
   */
  public void test_nameShadow_setter_method() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  set foo(x) {}",
            "  foo() {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 3, 3));
  }

  /**
   * Getter shadows field.
   */
  public void test_nameShadow_field_getter() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  var foo;",
            "  get foo() {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 7, 3));
  }

  /**
   * Getter shadows another getter.
   */
  public void test_nameShadow_getters() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  get foo() {}",
            "  set foo(x) {}",
            "  get foo() {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 5, 7, 3));
  }

  /**
   * Setter shadows another setter.
   */
  public void test_nameShadow_setters() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  set foo(x) {}",
            "  get foo() {}",
            "  set foo(x) {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 5, 7, 3));
  }

  /**
   * Field shadows getter.
   */
  public void test_nameShadow_getter_field() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  get foo() {}",
            "  var foo;",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 7, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 7, 3));
  }

  /**
   * Setter shadows method.
   */
  public void test_nameShadow_method_setter() {
    checkSourceErrors(
        makeCode(
            "// filler filler filler filler filler filler filler filler filler filler",
            "class A {",
            "  foo() {}",
            "  set foo(x) {}",
            "}"),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 3, 3, 3),
        errEx(ResolverErrorCode.DUPLICATE_MEMBER, 4, 7, 3));
  }

  public void testUnresolvedSuperFieldNegativeTest() {
    checkNumErrors("UnresolvedSuperFieldNegativeTest.dart", 1);
  }

  public void testStaticSuperFieldNegativeTest() {
    checkNumErrors("StaticSuperFieldNegativeTest.dart", 1);
  }

  public void testStaticSuperGetterNegativeTest() {
    checkNumErrors("StaticSuperGetterNegativeTest.dart", 1);
  }

  public void testStaticSuperMethodNegativeTest() {
    checkNumErrors("StaticSuperMethodNegativeTest.dart", 1);
  }

  public void testCyclicRedirectedConstructorNegativeTest() {
    checkNumErrors("CyclicRedirectedConstructorNegativeTest.dart", 3);
  }

  public void testConstRedirectedConstructorNegativeTest() {
    checkNumErrors("ConstRedirectedConstructorNegativeTest.dart", 1);
  }

  public void testRawTypesNegativeTest() {
    checkNumErrors("RawTypesNegativeTest.dart", 4);
  }

  private TestCompilerContext getContext() {
    return new TestCompilerContext() {
      @Override
      public void onError(DartCompilationError event) {
        errors.add(event);
      }
    };
  }
}
