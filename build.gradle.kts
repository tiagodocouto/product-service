/*
 * Copyright (c) 2023 Tiago do Couto.
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

@file:Suppress("UnstableApiUsage")

import info.solidsoft.gradle.pitest.PitestPluginExtension
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    // Project
    idea
    // Kotlin
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    // Spring
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.management)
    // Quality
    alias(libs.plugins.quality.versions)
    alias(libs.plugins.quality.catalog)
    // Test
    alias(libs.plugins.test.pitest)
}

repositories { mavenCentral() }

dependencies {
    // Development
    developmentOnly(libs.spring.dev.tools)
    // Annotations Processors
    annotationProcessor(libs.spring.boot.processor)
    // Spring
    implementation(libs.bundles.spring.boot)
    // Test
    testImplementation(libs.bundles.test.spring.boot)
    testImplementation(libs.bundles.test.kotest)
    pitest(libs.bundles.test.pitest)
}

java { sourceCompatibility = JavaVersion.VERSION_17 }

configurations {
    compileOnly { extendsFrom(configurations.annotationProcessor.get()) }
}

configure<PitestPluginExtension> {
    targetClasses.set(listOf(project["arcmutate.group"]))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = project["java.version"]
        freeCompilerArgs += project["java.args"]
    }
}

tasks.withType<Test>()
    .configureEach { useJUnitPlatform() }

tasks.register<Download>("arcmutateLicence") {
    src(project["arcmutate.license"])
    dest(project.buildFile("arcmutate.output"))
    onlyIfModified(true)
    overwrite(true)
}
