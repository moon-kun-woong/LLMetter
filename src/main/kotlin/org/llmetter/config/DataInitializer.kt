package org.llmetter.config

import org.llmetter.domain.user.AuthProvider
import org.llmetter.domain.user.User
import org.llmetter.domain.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(args: ApplicationArguments?) {
        initializeAdminUser()
    }

    private fun initializeAdminUser() {
        val adminEmail = "admin@llmetter.com"

        if (!userRepository.existsByEmail(adminEmail)) {
            val adminUser = User(
                email = adminEmail,
                provider = AuthProvider.ADMIN,
                passwordHash = passwordEncoder.encode("qwe123")
            )

            userRepository.save(adminUser)
            logger.info("Admin user created: $adminEmail")
        } else {
            logger.info("Admin user already exists: $adminEmail")
        }
    }
}
