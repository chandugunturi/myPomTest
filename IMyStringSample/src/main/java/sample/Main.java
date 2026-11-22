package sample;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            throw new IllegalArgumentException("Usage: Main <8|15|25> [library-directory]");
        }

        int version = Integer.parseInt(args[0]);
        Path libraryDirectory = args.length == 2 ? Paths.get(args[1])
                : Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                        .getParent().resolve("lib");
        try (Factory factory = Factory.load(version, libraryDirectory)) {
            IMyStringBuilder source = factory.createStringBuilder("Hello");
            source.append(" from ").append("IMyStringBuilder").append('!');
            System.out.println("Java " + version + " library: " + source);
        }
    }
}
