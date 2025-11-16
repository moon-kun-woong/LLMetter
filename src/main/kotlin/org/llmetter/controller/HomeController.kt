package org.llmetter.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @GetMapping("/")
    fun home(): Map<String, Any> {
        return mapOf(
            "service" to "LLMetter API Server",
            "version" to "1.0.0",
            "status" to "running",
            "endpoints" to mapOf(
                "login" to "POST /api/auth/login",
                "refresh" to "POST /api/auth/refresh",
                "logout" to "POST /api/auth/logout"
            ),
            "admin" to mapOf(
                "email" to "admin@llmetter.com",
                "password" to "qwe123"
            )
        )
    }

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf("status" to "UP")
    }
}
