package org.example

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

interface DummyStream<T> {
    fun generate(): Flux<T>
}

class RandomStringGenerator(
    private val name: String,
    private val delay: Duration = Duration.ZERO,
    private val limit: Int = Int.MAX_VALUE,
) : DummyStream<String> {

    override fun generate(): Flux<String> {
        return Flux.range(1, limit)
            .delayElements(delay)
            .flatMap { Mono.just("$name $it") }
    }
}