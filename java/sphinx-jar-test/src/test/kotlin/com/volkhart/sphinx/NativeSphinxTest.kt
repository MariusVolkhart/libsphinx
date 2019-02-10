package com.volkhart.sphinx

import com.google.common.truth.Truth.assertThat
import okio.ByteString
import org.junit.jupiter.api.Test

class NativeSphinxTest {

    /**
     * Ensure that native lib interactions work when packaged as a JAR
     */
    @Test
    fun jarPackaging() {
        val sphinx = NativeSphinx()
        val password = "password".toByteArray()

        val secret = ByteArray(32) { ' '.toByte() }

        val initial = sphinx.challenge(password)
        val response = sphinx.respond(initial.challenge, secret)
        val rwd = sphinx.finish(password, initial.blindingFactor, response)

        assertThat(ByteString.of(*rwd).hex()).isEqualTo("c36757d3ccebf402574f1812bdf589e3918366c4de1f0a7d8ee9ac8155d0e340")
    }
}