package org.example

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.RuntimeException
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

fun main(args: Array<String>) {
    val dummyStream1 = RandomStringGenerator("F", Duration.ofMillis(330))
    val dummyStream2 = RandomStringGenerator("S", Duration.ofMillis(710))
    val dummyStream3 = RandomStringGenerator("T", Duration.ofMillis(960))

    Flux.merge(dummyStream1.generate(), dummyStream2.generate(), fail())
//        .doOnNext { println("${dateTime()} - [${it.t1}] [${it.t2}] [${it.t3}]") }
        .doOnNext { println("${dateTime()} - [${it}]") }
        .blockLast()
}

private fun fail() = Flux.range(1, 100)
    .delayElements(Duration.ofMillis(500))
    .flatMap { if (it == 20) Mono.error(RuntimeException()) else Mono.just("FAIL $it") }

private fun dateTime(): String = LocalDateTime.now().format(formatter)