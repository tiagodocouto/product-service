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

package dev.docouto.helpers.extension

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.faker
import java.time.Instant

/**
 * A utility class providing extension functions to the Faker library.
 *
 * @see Faker
 */
object FakerExtensions {
    /**
     * A global instance of the [Faker] class, used for generating random data.
     *
     * @return The [Faker] instance.
     */
    val faker: Faker
        get() = faker { }

    /**
     * Generates a universally unique identifier (UUID).
     *
     * @return The generated UUID as a string.
     */
    fun Faker.uuid(): String =
        random.nextUUID()

    /**
     * Generates a random name using the Faker library.
     *
     * @return The generated name.
     */
    fun Faker.name(): String =
        worldOfWarcraft.hero()

    /**
     * Generates a random Instant object representing a point in time.
     *
     * @return A randomly generated Instant object.
     */
    fun Faker.instant(): Instant =
        Instant.ofEpochMilli(random.nextLong())
}
