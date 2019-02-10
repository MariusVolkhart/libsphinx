package com.volkhart.sphinx;

import okio.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

final class NativeLoader {
    private NativeLoader() {
        throw new AssertionError("No instances");
    }

    static void loadLibrary(String name) {
        loadLibrary(Runtime.getRuntime(), FileSystems.getDefault(), name);
    }

    static void loadLibrary(Runtime runtime, FileSystem fileSystem, String name) {
        String libOnDisk = extractNativeLibrary(runtime, fileSystem, name);
        try {
            runtime.load(libOnDisk);
        } catch (UnsatisfiedLinkError e) {
            if (e.getMessage().contains("GLIBC_")) {
                throw new Error("The version of GLIBC on the system is likely older than that required by Sphinx", e);
            }
            throw e;
        }
    }

    static String extractNativeLibrary(Runtime runtime, FileSystem fileSystem, String name) {

        // Since we're working with NIO FileSystem, we need to determine the temp directory
        Path tempDir = fileSystem.getPath(System.getProperty("java.io.tmpdir"));
        String pathToJarResource = getPathToLibraryInJar(name);

        // Format the filename somewhat nicely
        String nameWithExtension = pathToJarResource.substring(pathToJarResource.lastIndexOf("/") + 1);
        String[] parts = nameWithExtension.split("\\.", -1);
        nameWithExtension = parts[0] + "." + parts[1];

        try {

            // Ensure that the directory for native binary exists
            Files.createDirectories(tempDir);

            Path targetFile;
            if (isWindows()) {
                targetFile = prepareToExtractNativeLibraryWindows(tempDir, nameWithExtension);
            } else {
                targetFile = prepareToExtractNativeLibraryUnix(runtime, tempDir, nameWithExtension);
            }

            ByteString hash;
            try (BufferedSink sink = Okio.buffer(Okio.sink(targetFile))) {
                try (InputStream jarLib = NativeLoader.class.getResourceAsStream(pathToJarResource)) {
                    if (jarLib == null) {
                        throw new IllegalStateException("The JAR does not contain a resource at " + pathToJarResource + ". Sphinx is not able to run.");
                    }
                    HashingSource hashingSource;
                    try (Source source = Okio.source(jarLib)) {
                        hashingSource = HashingSource.sha1(source);
                        sink.writeAll(source);
                    }
                    hash = hashingSource.hash();
                }
            }

            if (isWindows()) {
                targetFile = postExtractWindows(hash, targetFile);
            }

            return targetFile.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write the Sphinx native library to disk. Sphinx cannot run.", e);
        }
    }

    private static Path postExtractWindows(ByteString hash, Path extractedLib) throws IOException {
        // To make the file easier to look at, we use the short hash similar to git
        String shortHash = hash.hex().substring(0, 6);

        // We're going to move the existing file to our hashed name variant:
        // ...\Temp\sphinxJni.dll -> ...\Temp\sphinxJni.<hash>.dll
        String fileName = extractedLib.getFileName().toString();
        String[] parts = fileName.split("\\.", -1);
        fileName = parts[0] + "." + shortHash + "." + parts[1];
        Path targetFile = extractedLib.getParent().resolve(fileName);

        if (Files.exists(targetFile)) {
            // Delete the file we just extracted - we don't need it
            Files.delete(extractedLib);
        } else {
            Files.move(extractedLib, targetFile);
        }

        return targetFile;
    }

    private static Path prepareToExtractNativeLibraryWindows(Path tempDir, String nameWithExtension) throws IOException {
        /*
         * On Windows we extract the library once per version, and then let multiple processes use the same version.
         * To do this we hash the native library, and include the hash in the filename. We also only write the file if it
         * doesn't already exist
         */
        Path targetFile = tempDir.resolve(nameWithExtension);
        if (!Files.exists(targetFile)) {
            return Files.createFile(targetFile);
        } else {
            return targetFile;
        }
    }

    private static Path prepareToExtractNativeLibraryUnix(Runtime runtime, Path tempDir, String nameWithExtension) throws IOException {

        final Path targetFile = Files.createTempFile(tempDir, nameWithExtension, null);

        // Register shutdown hook to clean up the file on exit (since it is unique to this process)
        runtime.addShutdownHook(new DeleteFileShutdownHook(targetFile));

        return targetFile;
    }

    private static String getPathToLibraryInJar(String name) {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        // OSX doesn't support 32 bit, so no need to do the bits check
        if (osName.contains("mac")) {
            return "osx_64/lib" + name + ".dylib";
        }

        String arch = System.getProperty("os.arch");
        if (isWindows()) {
            return "windows-" + arch + "/" + name + ".dll";
        } else {
            return arch + "/lib" + name + ".so";
        }
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return osName.contains("win");
    }
}
