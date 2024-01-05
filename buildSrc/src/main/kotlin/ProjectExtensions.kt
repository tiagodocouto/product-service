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

fun String.isStable(): Boolean =
    stableKeywords.any { this.uppercase().contains(it) }
            || stableRegex.matches(this)

fun ResolutionStrategy.kotlin(block: DependencyResolveDetails.() -> Unit) {
    eachDependency {
        if (requested.group == "org.jetbrains.kotlin")
            block()
    }
}

operator fun Project.get(name: String): String =
    properties(name)

fun Project.buildFile(name: String): File =
    layout.buildDirectory.file(properties(name)).get().asFile

fun Project.rootFile(name: String): File =
    layout.projectDirectory.file(properties(name)).asFile

private fun Project.properties(name: String): String =
    property("project.$name") as String
