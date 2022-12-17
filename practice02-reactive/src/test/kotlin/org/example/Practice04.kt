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

private fun getRandomRange(start: Int, end: Int): Long {
    return Random()
        .ints(start, end)
        .findFirst()
        .orElse(start)
        .toLong()
}

class Practice04 {

    @Test
    fun `should last offset commit when in request size`() {
        Flux.range(1, 1000).subscribeWith(BackPressureAndSinkSubscriber())
        Thread.sleep(9999)
    }

    class BackPressureAndSinkSubscriber : BaseSubscriber<Int>() {
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
                .flatMap { Mono.delay(Duration.ofMillis(getRandomRange(500, 1000))).flatMap { Mono.just(value) } }
                .subscribe {
                    println("${LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)} - $it next")
                    sink.emitNext(value, failHandler())
                    request(1)
                }
        }

        private fun failHandler() = Sinks.EmitFailureHandler { type, result ->
            if (result == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
                true
            } else {
                println("$type $result error")
                false
            }
        }
    }
}