package com.volkhart.sphinx

import com.google.common.jimfs.Jimfs
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Paths

class NativeLoaderTest {

    private lateinit var fileSystem: FileSystem
    private lateinit var runtime: Runtime

    @BeforeEach
    fun setUp() {
        runtime = Runtime.getRuntime()
        fileSystem = Jimfs.newFileSystem()
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    fun extractedFilesHaveUniqueNames() {
        val file1 = NativeLoader.extractNativeLibrary(runtime, fileSystem, libraryName)
        val file2 = NativeLoader.extractNativeLibrary(runtime, fileSystem, libraryName)
        assertThat(file1).isNotEqualTo(file2)
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun extractedFilesHaveIdenticalNames() {
        val file1 = NativeLoader.extractNativeLibrary(runtime, fileSystem, libraryName)
        val file2 = NativeLoader.extractNativeLibrary(runtime, fileSystem, libraryName)
        assertThat(file1).isEqualTo(file2)
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun extractedFilesHaveHashInName() {
        val file = NativeLoader.extractNativeLibrary(runtime, fileSystem, libraryName)
        val fileName = Paths.get(file).fileName.toString()

        // Allow anywhere between 6 and 20 hsh characters. The implementation might change.
        assertThat(fileName).matches("$libraryName\\.\\b[0-9a-f]{6,20}\\b\\.dll")
    }

    @Test
    fun extractedFileIsNot0Bytes() {
        val file = NativeLoader.extractNativeLibrary(runtime, fileSystem, libraryName)
        val size = Files.size(fileSystem.getPath(file))
        assertThat(size).isNotEqualTo(0)
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    fun shutdownHookToDeleteTemporaryFileIsRegistered() {
        runtime = mock(Runtime::class.java)
        NativeLoader.extractNativeLibrary(runtime, fileSystem, libraryName)
        verify(runtime, times(1)).addShutdownHook(any())
    }

    @Test
    fun insufficientGlibcVersionErrorsAreConveyedNicely() {
        runtime = mock(Runtime::class.java)
        `when`(runtime.load(any())).thenThrow(UnsatisfiedLinkError("version GLIBC_2.17 not found"))

        val error = assertThrows<Error> { NativeLoader.loadLibrary(runtime, fileSystem, libraryName) }
        assertThat(error).hasMessageThat().contains("older than that required by the Sphinx library")
    }

    companion object {
        const val libraryName = "sphinxJni"
    }
}