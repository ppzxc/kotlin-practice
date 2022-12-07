package org.example

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class Practice01 {
    @Test
    fun `should not catch when return error`() {
        StepVerifier.create(dummyGeneratorNotErrorControl(), 0)
            .expectSubscription()
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 1") }
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 2") }
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 3") }
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 4") }
            .thenRequest(1)
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun `should catch when return error`() {
        StepVerifier.create(dummyGeneratorErrorControl(), 0)
            .expectSubscription()
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 1") }
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 2") }
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 3") }
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("hi 4") }
            .thenRequest(1)
            .assertNext { assertThat(it).isEqualTo("error") }
            .thenRequest(5)
            .expectComplete()
            .verify()
    }

    private fun dummyGeneratorNotErrorControl() = Flux.range(1, 10)
        .flatMap { dummyPublisher(it) }
        .flatMap { dummyWrapper(it) }
        .flatMap { dummyAdder(it) }

    private fun dummyGeneratorErrorControl() = Flux.range(1, 10)
        .flatMap { dummyPublisher(it) }
        .flatMap { dummyWrapper(it) }
        .flatMap { dummyAdder(it) }
        .onErrorResume { Mono.just("error") }

    private fun dummyAdder(it: String) = Mono.just("hi $it")

    private fun dummyPublisher(it: Int) = if (it == 5) Mono.error(RuntimeException("5")) else Mono.just(it)

    private fun dummyWrapper(it: Int) = Mono.just(it.toString())
}