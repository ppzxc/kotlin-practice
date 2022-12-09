package org.example

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class Practice02 {
    @Test
    fun should() {
        producer()
            .log()
            .limitRate(2)
            .doOnNext { println(it) }
            .blockLast()
    }

    @Test
    fun should_2() {

    }

    private fun producer() = Flux.range(1, 100)
            .map { it.toString() }
}