package org.llmetter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LLMetterApplication

fun main(args: Array<String>) {
    runApplication<LLMetterApplication>(*args)
}