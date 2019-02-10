package com.volkhart.sphinx

import com.google.common.truth.Truth.assertThat
import okio.ByteString
import org.junit.jupiter.api.Test

class NativeSphinxTest {

    private val sphinx = NativeSphinx()

    /**
     * Sanity check that the challenge is working
     */
    @Test
    fun challenge() {
        val password = "password"
        val challenge = sphinx.challenge(password.toByteArray())
        assertThat(challenge.blindingFactor).isNotEqualTo(ByteArray(32))
        assertThat(challenge.challenge).isNotEqualTo(ByteArray(32))
    }

    /**
     * Mirrors `test.c` from sphinxlib
     */
    @Test
    fun integration() {
        // In C the password has a trailing NUL character. We need to add that in Java to get the same generated password
        val password = "shitty password"
        val passwordBytes = ByteArray(16)
        System.arraycopy(password.toByteArray(), 0, passwordBytes, 0, password.length)

        val secret = ByteArray(32) { ' '.toByte() }

        val initial = sphinx.challenge(passwordBytes)
        val response = sphinx.respond(initial.challenge, secret)
        val rwd = sphinx.finish(passwordBytes, initial.blindingFactor, response)

        assertThat(ByteString.of(*rwd).hex()).isEqualTo("0e7c3b3d1fb91a6ac4fede91b5a175da03c841259a2066928deed1810bdab32e")
    }
}