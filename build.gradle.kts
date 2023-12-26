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
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
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
    alias(libs.plugins.quality.detekt)
    // spotless
    // Test
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.test.pitest)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Development
    developmentOnly(libs.spring.dev.tools)
    // Annotations Processors
    annotationProcessor(libs.spring.boot.processor)
    // Spring
    implementation(libs.bundles.spring.boot)
    // Test & Quality
    testImplementation(libs.bundles.test.spring.boot)
    testImplementation(libs.bundles.test.kotest)
    pitest(libs.bundles.test.pitest)
}

// Compiler
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly { extendsFrom(configurations.annotationProcessor.get()) }
}

tasks.withType<KotlinCompile>()
    .configureEach {
        kotlinOptions {
            jvmTarget = project["java.version"]
            freeCompilerArgs += project["java.args"]
        }
    }

// Test & Quality
configurations.matching { it.name == "detekt" }.all {
    resolutionStrategy.kotlin { useVersion(getSupportedKotlinVersion()) }
}

tasks.check {
    dependsOn(
        "detekt",
    )
}

kover {
    koverReport {
        defaults {
            xml { onCheck = true }
            html { onCheck = true }
            verify { onCheck = true }
        }
    }
}

configure<PitestPluginExtension> {
    targetClasses.set(listOf(project["arcmutate.group"]))
}

tasks.withType<Test>()
    .configureEach { useJUnitPlatform() }

tasks.withType<Detekt>()
    .configureEach {
        config.setFrom("$rootDir/detekt-config.yml")
        buildUponDefaultConfig = true
        allRules = true
        reports {
            xml.required.set(true)
            html.required.set(true)
            sarif.required.set(true)
        }
    }

// Custom Tasks
tasks.register<Download>("arcmutateLicense") {
    src(project["arcmutate.license"])
    dest(project.buildFile("arcmutate.output"))
    onlyIfModified(true)
    overwrite(true)
}
