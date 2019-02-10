package com.volkhart.sphinx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class DeleteFileShutdownHook extends Thread {

    DeleteFileShutdownHook(Path toDelete) {
        super(() -> {
            try {
                Files.deleteIfExists(toDelete);
            } catch (IOException e) {
                // Nothing we can do at this point. The JVM is exiting, and besides, cleaning up was more of a
                // "nice to have".
            }
        }, DeleteFileShutdownHook.class.getSimpleName());
    }
}
