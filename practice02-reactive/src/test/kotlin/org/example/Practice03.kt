package org.example

import org.junit.jupiter.api.Test
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class Practice03 {

    @Test
    fun `should last offset commit when in request size`() {
        Flux.range(1, 1000)
            .flatMap { value -> Mono.delay(Duration.ofMillis(getRandomRange(1000, 1200))).map { value } }
//            .log()
            .subscribeWith(TestSubscriber())
        Thread.sleep(9999)
    }

    private fun getRandomRange(start: Int, end: Int): Long {
        return Random()
            .ints(start, end)
            .findFirst()
            .orElse(start)
            .toLong()
    }

    class TestSubscriber : BaseSubscriber<Int>() {
        private val sink = Sinks.many().unicast().onBackpressureBuffer<Int>()

        init {
            sink.asFlux()
                .reduce(-1) { last, current -> if (last < current) commit(current) else last }
                .subscribe()
        }

        private fun commit(value: Int): Int {
            println("${LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)} - $value ## commit")
            return value
        }

        override fun hookOnSubscribe(subscription: Subscription) = subscription.request(3)

        override fun hookOnNext(value: Int) {
            Mono.just(value)
                .subscribe {
                    println("${LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)} - $it next")
                    sink.tryEmitNext(value).orThrow()
                    request(1)
                }
        }
    }
}