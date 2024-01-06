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

@file:Suppress("UnstableApiUsage")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import info.solidsoft.gradle.pitest.PitestPluginExtension
import info.solidsoft.gradle.pitest.PitestTask
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
    alias(libs.plugins.test.pitest.github)
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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kover {
    koverReport {
        filters {
            includes { classes(project["base.package"]) }
            excludes { project["kover.exclude"].array().map { classes(it) } }
        }
        defaults {
            xml { setReportFile(project.rootFile("kover.output")) }
        }
    }
}

configure<PitestPluginExtension> {
    mutators = listOf("STRONGER")
    features = listOf("+GIT(from[HEAD~1])", "+gitci")
    threads = Runtime.getRuntime().availableProcessors()
    targetClasses.set(listOf(project["base.package"]))
    outputFormats = listOf("XML", "GITCI")
    failWhenNoMutations = false
}

configurations {
    compileOnly { extendsFrom(configurations.annotationProcessor.get()) }
}.matching { it.name == "detekt" }.all {
    resolutionStrategy.kotlin { useVersion(getSupportedKotlinVersion()) }
}

tasks {
    test {
        useJUnitPlatform()
        dependsOn(
            "detekt",
            "pitest"
        )
        finalizedBy("koverXmlReport")
    }

    withType<KotlinCompile>()
        .configureEach {
            kotlinOptions {
                jvmTarget = project["java.version"]
                freeCompilerArgs += project["java.args"]
            }
        }

    withType<Detekt>()
        .configureEach {
            allRules = true
            buildUponDefaultConfig = true
            config.setFrom("$rootDir/detekt-config.yml")
            reports { sarif.required.set(true) }
        }

    withType<DependencyUpdatesTask>()
        .configureEach {
            rejectVersionIf { candidate.version.isStable().not() }
        }

    withType<PitestTask>()
        .configureEach { dependsOn("arcmutateLicense") }

    register<Download>("arcmutateLicense") {
        src(project["arcmutate.license"])
        dest(project.buildFile("arcmutate.output"))
        onlyIfModified(true)
        overwrite(true)
    }
}
