package com.volkhart.sphinx

import com.google.common.jimfs.Jimfs
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class DeleteFileShutdownHookTest {

    @Test
    fun hookHasUsefulName() {
        val path = Paths.get("test.tmp")
        val hook = DeleteFileShutdownHook(path)
        assertThat(hook.name).doesNotContain("Thread")
    }

    @Test
    fun hookDeletesFileIfItExists() {
        val fileSystem = Jimfs.newFileSystem()

        // Create the file
        val path = fileSystem.getPath("test.txt")
        Files.write(path, "hello, world".toByteArray())

        // Run the hook - this should delete the file
        val hook = DeleteFileShutdownHook(path)
        hook.run()

        assertThat(Files.notExists(path)).isTrue()
    }

    @Test
    fun hookExitsGracefullyIfFileDoesNotExist() {
        val fileSystem = Jimfs.newFileSystem()
        val path = fileSystem.getPath("test.txt")

        // Run the hook - this should not throw or complain
        val hook = DeleteFileShutdownHook(path)
        hook.run()

        assertThat(Files.notExists(path)).isTrue()
    }
}