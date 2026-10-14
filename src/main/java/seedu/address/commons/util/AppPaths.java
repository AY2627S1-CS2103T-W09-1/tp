package seedu.address.commons.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * Locates the application's home folder and confines packaged-app data to it.
 */
public final class AppPaths {

    private static final Path CODE_LOCATION = findCodeLocation();
    private static final boolean IS_PACKAGED = !Files.isDirectory(CODE_LOCATION);
    private static final Path HOME_FOLDER = findHomeFolder();

    private AppPaths() {
    }

    /**
     * Returns the folder containing the JAR, or the working folder for development runs.
     */
    public static Path getHomeFolder() {
        return HOME_FOLDER;
    }

    /**
     * Resolves a data path inside the JAR home folder for packaged runs.
     * Development runs have no JAR home and retain support for test fixture paths.
     *
     * @throws IOException If a packaged data path escapes the home folder.
     */
    public static Path resolveDataPath(Path path) throws IOException {
        return IS_PACKAGED ? resolveWithinHome(HOME_FOLDER, path) : path;
    }

    /**
     * Resolves a path inside a home folder, rejecting traversal and outward symbolic links.
     *
     * @throws IOException If the path or any existing ancestor escapes the home folder.
     */
    public static Path resolveWithinHome(Path homeFolder, Path path) throws IOException {
        Path realHome = homeFolder.toRealPath();
        Path resolved = (path.isAbsolute() ? path : realHome.resolve(path)).toAbsolutePath().normalize();
        if (!resolved.startsWith(realHome)) {
            throw new IOException("Application data must remain inside the JAR home folder.");
        }
        Path current = realHome;
        for (Path component : realHome.relativize(resolved)) {
            current = current.resolve(component);
            if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
                    && !current.toRealPath().startsWith(realHome)) {
                throw new IOException("Application data must not follow links outside the JAR home folder.");
            }
        }
        return resolved;
    }

    /**
     * Configures JavaFX's native-library cache and temporary files inside the home folder.
     * These are process-local Java properties, not changes to OS configuration.
     *
     * @throws IOException If a cache folder is unsafe or cannot be used.
     */
    public static void configureRuntimeCaches() throws IOException {
        Path cache = prepareRuntimeFolder(".javafx");
        Path temporary = prepareRuntimeFolder(".tmp");
        System.setProperty("javafx.cachedir", cache.toString());
        System.setProperty("java.io.tmpdir", temporary.toString());
    }

    /**
     * Creates a usable runtime folder and checks its existing entries for outward links.
     */
    private static Path prepareRuntimeFolder(String name) throws IOException {
        Path folder = resolveWithinHome(HOME_FOLDER, Path.of(name));
        Files.createDirectories(folder);
        try (var entries = Files.walk(folder)) {
            for (Path entry : entries.toList()) {
                resolveWithinHome(HOME_FOLDER, entry);
            }
        }
        if (!Files.isReadable(folder) || !Files.isWritable(folder)) {
            throw new IOException("The JAR home folder must be readable and writable.");
        }
        return folder;
    }

    /**
     * Returns the real home folder, including when its parent path contains symbolic links.
     */
    private static Path findHomeFolder() {
        Path home = IS_PACKAGED ? CODE_LOCATION.getParent() : Path.of("");
        try {
            return home.toRealPath();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot access the application home folder.", exception);
        }
    }

    /**
     * Returns the location from which this application's classes were loaded.
     */
    private static Path findCodeLocation() {
        try {
            return Path.of(AppPaths.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .toAbsolutePath().normalize();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Cannot determine the application home folder.", exception);
        }
    }
}
