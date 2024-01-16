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

package dev.docouto.helpers.spec

import dev.docouto.helpers.config.MongoDBConfig
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Wraps a few extra things on top of [Kotest ExpectSpec][TestSpec]
 * Use it for Integration Tests purpose, as it will start the database and
 * everything else needed by the main application to start
 *
 * @see TestSpec
 *
 * Usage:
 * ```kotlin
 * @IntegrationTestContext
 * class SomeTests : IntegrationTestSpec() {
 *     @Test
 *     fun `any test`() { ... }
 * }
 *```
 */
@Testcontainers
abstract class IntegrationTestSpec : TestSpec() {
    companion object {
        /**
         * Sets the [Mongo container][MongoDBConfig] url on the property registry
         *
         * @param registry the property registry
         * @see DynamicPropertyRegistry
         */
        @JvmStatic
        @DynamicPropertySource
        fun dynamicPropertySource(registry: DynamicPropertyRegistry) {
            MongoDBConfig.registry(registry)
        }
    }
}