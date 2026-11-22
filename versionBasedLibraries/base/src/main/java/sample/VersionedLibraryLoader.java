package sample;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class VersionedLibraryLoader implements Closeable {
    private final Path jar;
    private final JarClassLoader loader;
    private boolean closed;

    private VersionedLibraryLoader(Path jar, JarClassLoader loader) {
        this.jar = jar;
        this.loader = loader;
    }

    public static VersionedLibraryLoader load(Path jar) throws IOException {
        Path location = Objects.requireNonNull(jar, "jar").toAbsolutePath();
        if (!Files.isRegularFile(location)) {
            throw new IOException("Library JAR not found: " + location);
        }
        return new VersionedLibraryLoader(location, new JarClassLoader(location.toUri().toURL()));
    }

    public <T> T create(String implementationName, Class<T> contract,
            Class<?>[] parameterTypes, Object... arguments) {
        if (closed) {
            throw new IllegalStateException("Library is closed");
        }

        try {
            Class<? extends T> implementation = loader.findImplementation(implementationName).asSubclass(contract);
            Constructor<? extends T> constructor = implementation.getConstructor(parameterTypes);
            return constructor.newInstance(arguments);
        } catch (InvocationTargetException failure) {
            Throwable cause = failure.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException("Constructor failed for " + implementationName, cause);
        } catch (ReflectiveOperationException | ClassCastException failure) {
            throw new IllegalArgumentException("Cannot create " + implementationName
                    + " as " + contract.getName() + " from " + jar, failure);
        }
    }

    @Override
    public void close() throws IOException {
        closed = true;
        loader.close();
    }

    private static final class JarClassLoader extends URLClassLoader {
        private JarClassLoader(URL jar) {
            super(new URL[] {jar}, VersionedLibraryLoader.class.getClassLoader());
        }

        private Class<?> findImplementation(String name) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> implementation = findLoadedClass(name);
                if (implementation != null && implementation.getClassLoader() != this) {
                    throw new ClassNotFoundException(name + " is not defined in the selected JAR");
                }
                return implementation == null ? findClass(name) : implementation;
            }
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("java.")) {
                return super.loadClass(name, resolve);
            }

            // Prefer this JAR for implementations and helpers; shared base types come from the parent.
            synchronized (getClassLoadingLock(name)) {
                Class<?> implementation;
                try {
                    implementation = findImplementation(name);
                } catch (ClassNotFoundException missingFromJar) {
                    return super.loadClass(name, resolve);
                }
                if (resolve) {
                    resolveClass(implementation);
                }
                return implementation;
            }
        }
    }
}
