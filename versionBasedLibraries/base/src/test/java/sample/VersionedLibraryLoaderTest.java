package sample;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VersionedLibraryLoaderTest {
    @TempDir
    Path directory;

    @Test
    void rejectsUnsupportedVersionsAndMissingLibraries() {
        assertThrows(IllegalArgumentException.class, () -> Factory.load(9, directory));
        IOException failure = assertThrows(IOException.class, () -> Factory.load(8, directory));
        assertTrue(failure.getMessage().contains("versioned-library-java8.jar"), failure.getMessage());
    }

    @Test
    void rejectsInvalidImplementationAndReleasesTheJar() throws IOException {
        Path jar = directory.resolve("empty.jar");
        try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
            output.finish();
        }

        try (VersionedLibraryLoader loader = VersionedLibraryLoader.load(jar)) {
            IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                    () -> loader.create("sample.MyStringBuilder", IMyStringBuilder.class, new Class<?>[0]));
            assertTrue(failure.getMessage().contains("sample.MyStringBuilder"), failure.getMessage());
        }
        Files.delete(jar);
    }
}
