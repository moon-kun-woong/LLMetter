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
        val adminPassword = "qwe123"

        val existingUser = userRepository.findByEmail(adminEmail)

        if (existingUser.isPresent) {
            val user = existingUser.get()
            val expectedHash = passwordEncoder.encode(adminPassword)

            // 비밀번호 검증 - 일치하지 않으면 업데이트
            if (user.passwordHash == null || !passwordEncoder.matches(adminPassword, user.passwordHash)) {
                logger.warn("Admin user password mismatch detected. Recreating admin user...")
                userRepository.delete(user)

                val newAdminUser = User(
                    email = adminEmail,
                    provider = AuthProvider.ADMIN,
                    passwordHash = passwordEncoder.encode(adminPassword)
                )
                userRepository.save(newAdminUser)
                logger.info("Admin user recreated with correct password: $adminEmail")
            } else {
                logger.info("Admin user already exists with correct password: $adminEmail")
            }
        } else {
            val adminUser = User(
                email = adminEmail,
                provider = AuthProvider.ADMIN,
                passwordHash = passwordEncoder.encode(adminPassword)
            )

            userRepository.save(adminUser)
            logger.info("Admin user created: $adminEmail")
        }
    }
}
