/*
 * Copyright (c) 2023-2024 Tiago do Couto.
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

import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ResolutionStrategy
import java.io.File

private val stableKeywords = listOf("RELEASE", "FINAL", "GA")
private val stableRegex = "^[0-9,.v-]+(-r)?$".toRegex()

/**
 * Checks if a `Version` is considered stable.
 *
 * @return true if the `Version` is stable, false otherwise.
 */
fun String.isStable(): Boolean =
    stableKeywords.any { this.uppercase().contains(it) }
            || stableRegex.matches(this)

/**
 * Applies a resolution strategy to each dependency for `Kotlin` related dependencies.
 * This method iterates over each dependency and executes the provided block of code if the dependency
 * belongs to the "org.jetbrains.kotlin" group. The block of code is executed within the
 * `DependencyResolveDetails` receiver object, allowing you to access and modify the details of the dependency.
 *
 * @param block The code block to execute for each dependency.
 *
 * @see DependencyResolveDetails
 */
fun ResolutionStrategy.kotlin(block: DependencyResolveDetails.() -> Unit) {
    eachDependency {
        if (requested.group == "org.jetbrains.kotlin") block()
    }
}

/**
 * Returns the value of the property with the given project.[name] from this [Project].
 *
 * @param name The name of the property.
 * @return The value of the property with the given project.[name].
 */
operator fun Project.get(name: String): String =
    properties(name)

/**
 * Invokes the given lambda on each element of the property with the given project.[name] from this [Project].
 *
 * @param block the lambda to be executed on each element of the array
 */
operator fun String.invoke(block: (String) -> Unit) =
    array().map(block)

/**
 * Filters and maps properties of the project that have keys starting with "project." followed by the given [name].
 *
 * @param name The name to filter the properties by. Only properties with keys starting with project.[name] will be considered.
 * @param block The block of code to apply to each filtered property. It takes a key (substring after "project.") and a value as parameters.
 *              The value is parsed as a comma separated list of values, if ends with "comma", or string.
 */
fun Project.all(name: String, block: (String, Any) -> Unit) =
    properties
        .filter { it.key.startsWith("project.$name") }
        .map { block(it.key.substringAfter("project."), it.value.extract()) }

/**
 * Gets a file from the build directory with the given [name].
 *
 * @param name The name of the file.
 * @return the path to the file on build directory.
 */
fun Project.buildFile(name: String): File =
    layout.buildDirectory.file(properties(name)).get().asFile

/**
 * Returns the root file of the project with the given name.
 *
 * @param name the name of the file
 * @return the root file with the given name
 */
fun Project.rootFile(name: String): File =
    layout.projectDirectory.file(properties(name)).asFile

/**
 * Extracts the value of the current object, assuming it is a string.
 * If the string ends with a comma, it is parsed as an array of values.
 * Otherwise, it is returned as is.
 *
 * @return The extracted value, either a string or an array.
 */
private fun Any?.extract(): Any =
    (this as String).let {
        if (it.endsWith(",")) it.array()
        else it
    }

/**
 * Splits the string into a list of strings using comma as a delimiter,
 *
 * @return The mutable list of non-blank strings.
 */
private fun String.array(): MutableList<String> =
    split(",")
        .filter { it.isNotBlank() }
        .toMutableList()

/**
 * Returns the value of the property with the given project.[name] from this [Project].
 *
 * @param name The name of the property.
 * @return The value of the property with the given project.[name].
 */
private fun Project.properties(name: String): String =
    property("project.$name") as String
