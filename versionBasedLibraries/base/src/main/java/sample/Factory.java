package sample;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class Factory implements Closeable {
    private final VersionedLibraryLoader loader;

    private Factory(VersionedLibraryLoader loader) {
        this.loader = loader;
    }

    public static Factory load(int version, Path libraryDirectory) throws IOException {
        if (version != 8 && version != 15 && version != 25) {
            throw new IllegalArgumentException("Supported library versions are 8, 15, and 25");
        }

        String specification = System.getProperty("java.specification.version");
        int runtimeVersion = Integer.parseInt(specification.startsWith("1.")
                ? specification.substring(2) : specification);
        if (runtimeVersion < version) {
            throw new IllegalArgumentException("Java " + version
                    + " library requires Java " + version + " or later; current runtime is Java " + runtimeVersion);
        }

        Path jar = Objects.requireNonNull(libraryDirectory, "libraryDirectory")
                .resolve("versioned-library-java" + version + ".jar");
        return new Factory(VersionedLibraryLoader.load(jar));
    }

    public IMyStringBuilder createStringBuilder() {
        return loader.create("sample.MyStringBuilder", IMyStringBuilder.class, new Class<?>[0]);
    }

    public IMyStringBuilder createStringBuilder(int capacity) {
        return loader.create("sample.MyStringBuilder", IMyStringBuilder.class,
                new Class<?>[] {int.class}, capacity);
    }

    public IMyStringBuilder createStringBuilder(String initialValue) {
        return loader.create("sample.MyStringBuilder", IMyStringBuilder.class,
                new Class<?>[] {String.class}, initialValue);
    }

    public IMyStringBuilder createStringBuilder(CharSequence initialValue) {
        return loader.create("sample.MyStringBuilder", IMyStringBuilder.class,
                new Class<?>[] {CharSequence.class}, initialValue);
    }

    @Override
    public void close() throws IOException {
        loader.close();
    }
}
