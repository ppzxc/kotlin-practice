package org.example

fun main(args: Array<String>) {
    val tt: Any? = null

//    tt?.let { println("NOT NULL") } ?: run { println("NULL") }

    val result = tt?.toString() ?: "FF"
    println(result)
}