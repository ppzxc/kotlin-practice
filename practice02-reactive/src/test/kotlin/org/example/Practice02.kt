package org.example

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class Practice02 {
    @Test
    fun should() {
        producer()
            .log()
            .limitRate(10)
            .doOnNext { println(it) }
            .blockLast()
    }

    private fun producer() = Flux.range(1, 100)
            .map { it.toString() }
}