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

package dev.docouto.helpers.rule

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.Source
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent.violated
import dev.docouto.helpers.extension.ListExtensions.isMissing
import java.util.*
import kotlin.jvm.optionals.getOrNull

private const val SUFFIX_TEST = "Tests"
private const val SUFFIX_KT = "Kt"
private const val SUFFIX_EXTENSION = "Extensions"
private const val SUFFIX_EXCEPTION = "Exception"
private const val TEST_BUILD_PATH = "build/classes/kotlin/test/"
private const val IS_ANONYMOUS_CLASS = "$"
typealias TestClassesBySimpleClassName = Map<String, List<JavaClass>>

/**
 * [ArchConditionShouldHaveTestForClass] checks if a class has a test class.
 */
object ArchConditionShouldHaveTestForClass : ArchCondition<JavaClass>("should have test for class") {
    private lateinit var testClassesBySimpleClassName: TestClassesBySimpleClassName

    /**
     * Determines whether a Java class should be filtered out.
     * A Java class should be filtered out if its name ends with a specific suffix, and it is not an anonymous class.
     *
     * @return `true` if the Java class should be filtered out, `false` otherwise.
     */
    private val JavaClass.shouldFilterOut: Boolean
        get() = name.endsWith(SUFFIX_TEST) &&
            !isAnonymousClass

    /**
     * Determines if the specified JavaClass should be ignored.
     *
     * @return true if the JavaClass should be ignored, false otherwise
     */
    private val JavaClass.shouldIgnore: Boolean
        get() = simpleName.endsWith(SUFFIX_TEST) ||
            simpleName.endsWith(SUFFIX_KT) ||
            simpleName.endsWith(SUFFIX_EXTENSION) ||
            simpleName.endsWith(SUFFIX_EXCEPTION) ||
            name.contains(IS_ANONYMOUS_CLASS) ||
            source.isTestSource ||
            isInterface ||
            isAnonymousClass

    /**
     * Determines whether the given optional source is a test source.
     *
     * @return true if the source is a test source, false otherwise.
     */
    private val Optional<Source>.isTestSource: Boolean
        get() = getOrNull()?.uri?.path?.contains(TEST_BUILD_PATH) ?: false

    /**
     * Initializes the testing process.
     *
     * @param allObjectsToTest A collection of JavaClass objects to be tested.
     */
    override fun init(allObjectsToTest: MutableCollection<JavaClass>) {
        testClassesBySimpleClassName = allObjectsToTest.filter { it.shouldFilterOut }
            .groupBy { clazz -> clazz.simpleName }
    }

    /**
     * Checks if the given Java class has a corresponding test class.
     *
     * @param item the Java class to be checked
     * @param events the collection of condition events to add violations to
     */
    override fun check(item: JavaClass, events: ConditionEvents) {
        if (item.shouldIgnore) return
        testClassesBySimpleClassName[item.simpleName + SUFFIX_TEST].isMissing {
            events.add(violated(item, "${item.name} does not have a test class"))
        }
    }
}
