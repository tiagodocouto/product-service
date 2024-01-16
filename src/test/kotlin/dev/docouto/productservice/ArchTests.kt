/*
 * Copyright (c) 2024 Tiago do Couto.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.docouto.productservice

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.Priority
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.library.GeneralCodingRules
import dev.docouto.helpers.rule.ArchConditionShouldHaveTestForClass
import dev.docouto.helpers.spec.TestSpec

private const val SUFFIX_EXCEPTION = "Exception"

/**
 * ArchTests class is a test specification used to perform architectural tests over the project.
 */
@AnalyzeClasses(packages = ["dev.docouto.productservice"])
class ArchTests : TestSpec() {
    /**
     * Certify that no Classes throws any kind of generic [Exception]
     */
    @ArchTest
    val noClassesShouldThrowGenericExceptions: ArchRule =
        GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS

    /**
     * Certify that no Classes uses generic java logging
     */
    @ArchTest
    val noClassesShouldUseJavaUtilLogging: ArchRule =
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING

    /**
     * Certify that no Classes use org.joda.time
     */
    @ArchTest
    val noClassesShouldUseJodaTime: ArchRule =
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME

    /**
     * Certify that no Classes use field injection
     * it should use constructor injection
     */
    @ArchTest
    val noClassesShouldUseFieldInjectionAnnotation: ArchRule =
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION

    /**
     * Certify that all Classes have a Test
     */
    @ArchTest
    val allClassShouldHaveTest: ArchRule =
        ArchRuleDefinition.priority(Priority.HIGH)
            .classes().should(ArchConditionShouldHaveTestForClass)
            .`as`("All classes should have test")

    /**
     * Certify that all tests are in the same package as their classes
     */
    @ArchTest
    val testClassesShouldResideInTheSamePackageAsImplementation: ArchRule =
        GeneralCodingRules.testClassesShouldResideInTheSamePackageAsImplementation()

    /**
     * Certify that EXCEPTIONS extend Extension
     */
    @ArchTest
    val exceptionsShouldHaveExceptionAsSuffix: ArchRule =
        ArchRuleDefinition.priority(Priority.HIGH)
            .classes().that().areAssignableTo(Exception::class.java)
            .should().haveSimpleNameEndingWith(SUFFIX_EXCEPTION)
}
