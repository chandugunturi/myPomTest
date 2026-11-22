package sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import samplefixtures.EchoTask;

@Tag("Integration")
class LibrarySelectionTest {
    private final Path directory = Paths.get(System.getProperty("mystring.library.directory"));

    @ParameterizedTest
    @ValueSource(ints = {8, 15, 25})
    void loadsTheRequestedJarAndRunsItsApis(int version) throws Exception {
        try (Factory factory = Factory.load(version, directory)) {
            IMyStringBuilder builder = factory.createStringBuilder("Hello");
            Path loadedJar = Paths.get(builder.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            assertEquals(directory.resolve("versioned-library-java" + version + ".jar").toRealPath(),
                    loadedJar.toRealPath());
            assertSame(builder, builder.append('!'));
            assertEquals("Hello!", builder.toString());

            if (version == 8) {
                assertThrows(UnsupportedOperationException.class, () -> builder.isEmpty());
                assertThrows(UnsupportedOperationException.class,
                        () -> builder.compareTo(new StringBuilder("Hello!")));
            } else {
                assertEquals(0, builder.compareTo(new StringBuilder("Hello!")));
                builder.setLength(0);
                assertTrue(builder.isEmpty());
            }

            if (version == 25) {
                assertSame(builder, builder.repeat("x", 2).repeat('y', 2));
                assertEquals("xxyy", builder.toString());
            } else {
                assertThrows(UnsupportedOperationException.class, () -> builder.repeat("x", 2));
                assertThrows(UnsupportedOperationException.class, () -> builder.repeat('y', 2));
            }
            assertThrows(NullPointerException.class, () -> factory.createStringBuilder((String) null));
            assertEquals("", factory.createStringBuilder().toString());
            assertEquals(32, factory.createStringBuilder(32).capacity());
            assertEquals("sequence", factory.createStringBuilder((CharSequence) new StringBuilder("sequence"))
                    .toString());
        }
    }

    @Test
    void differentVersionsCanCoexistWithoutSharingTheirConcreteClass() throws Exception {
        try (Factory java8 = Factory.load(8, directory);
                Factory java25 = Factory.load(25, directory)) {
            IMyStringBuilder older = java8.createStringBuilder("old");
            IMyStringBuilder newer = java25.createStringBuilder("new");
            assertNotSame(older.getClass(), newer.getClass());
            assertThrows(UnsupportedOperationException.class, () -> older.repeat("x", 2));
            assertEquals("newxx", newer.repeat("x", 2).toString());
            assertEquals("old", older.toString());
        }
    }

    @Test
    void closedLibrariesCannotCreateMoreBuilders() throws Exception {
        Factory factory = Factory.load(8, directory);
        try (Factory owned = factory) {
            assertEquals("abc", owned.createStringBuilder("abc").toString());
        }
        assertThrows(IllegalStateException.class, () -> factory.createStringBuilder("abc"));
    }

    @Test
    void genericLoaderCreatesDifferentTypesFromTheSameJar(@TempDir Path temporary) throws Exception {
        Path combined = temporary.resolve("combined.jar");
        Files.copy(directory.resolve("versioned-library-java8.jar"), combined);
        try (FileSystem archive = FileSystems.newFileSystem(URI.create("jar:" + combined.toUri()),
                Collections.<String, Object>emptyMap());
                InputStream fixture = EchoTask.class.getResourceAsStream("/samplefixtures/EchoTask.class")) {
            Path entry = archive.getPath("/samplefixtures/EchoTask.class");
            Files.createDirectories(entry.getParent());
            Files.copy(fixture, entry);
        }

        try (VersionedLibraryLoader loader = VersionedLibraryLoader.load(combined)) {
            IMyStringBuilder builder = loader.create("sample.MyStringBuilder", IMyStringBuilder.class,
                    new Class<?>[] {String.class}, "builder");
            Callable<?> task = loader.create("samplefixtures.EchoTask", Callable.class,
                    new Class<?>[] {String.class}, "task");
            assertEquals("builder!", builder.append('!').toString());
            assertEquals("task", task.call());
            assertSame(builder.getClass().getClassLoader(), task.getClass().getClassLoader());
            assertNotSame(EchoTask.class, task.getClass());
            assertThrows(IllegalArgumentException.class, () -> loader.create("sample.MyStringBuilder",
                    Callable.class, new Class<?>[] {String.class}, "invalid"));
            assertThrows(IllegalArgumentException.class, () -> loader.create("java.lang.String",
                    String.class, new Class<?>[] {String.class}, "not in the jar"));
        }
    }
}
