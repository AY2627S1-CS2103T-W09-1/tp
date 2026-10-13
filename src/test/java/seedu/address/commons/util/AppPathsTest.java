package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests confinement to the application home folder, including symbolic links.
 */
public class AppPathsTest {

    @TempDir
    public Path temporaryFolder;

    /**
     * Checks that missing data files resolve under the home folder.
     */
    @Test
    public void resolveWithinHome_relativePath_resolvesInsideHome() throws IOException {
        Path home = Files.createDirectory(temporaryFolder.resolve("home")).toRealPath();
        assertEquals(home.resolve("data/addressbook.json"),
                AppPaths.resolveWithinHome(home, Path.of("data/addressbook.json")));
        assertEquals(home.resolve("preferences.json"),
                AppPaths.resolveWithinHome(home, home.resolve("preferences.json")));
    }

    /**
     * Checks that traversal and absolute paths outside the home are rejected.
     */
    @Test
    public void resolveWithinHome_outsidePath_throwsIoException() throws IOException {
        Path home = Files.createDirectory(temporaryFolder.resolve("home"));
        assertThrows(IOException.class, () -> AppPaths.resolveWithinHome(home, Path.of("../outside.json")));
        assertThrows(IOException.class, () -> AppPaths.resolveWithinHome(home,
                temporaryFolder.resolve("outside.json")));
    }

    /**
     * Checks that both outward file links and outward directory links are rejected.
     */
    @Test
    public void resolveWithinHome_outwardLink_throwsIoException() throws IOException {
        Path home = Files.createDirectory(temporaryFolder.resolve("home"));
        Path outside = Files.createDirectory(temporaryFolder.resolve("outside"));
        Path outsideFile = Files.writeString(outside.resolve("contact.json"), "unchanged");
        createSymbolicLink(home.resolve("data"), outside);
        createSymbolicLink(home.resolve("preferences.json"), outsideFile);
        assertThrows(IOException.class, () -> AppPaths.resolveWithinHome(home,
                Path.of("data/contact.json")));
        assertThrows(IOException.class, () -> AppPaths.resolveWithinHome(home,
                Path.of("preferences.json")));
        assertEquals("unchanged", Files.readString(outsideFile));
    }

    /**
     * Checks that links to data within the same home folder remain usable.
     */
    @Test
    public void resolveWithinHome_inwardLink_resolvesInsideHome() throws IOException {
        Path home = Files.createDirectory(temporaryFolder.resolve("home")).toRealPath();
        Path data = Files.createDirectory(home.resolve("records"));
        createSymbolicLink(home.resolve("data"), data);
        assertEquals(home.resolve("data/contact.json"),
                AppPaths.resolveWithinHome(home, Path.of("data/contact.json")));
    }

    /**
     * Creates a test link, skipping link-specific tests on systems without link privileges.
     */
    private static void createSymbolicLink(Path link, Path target) {
        try {
            Files.createSymbolicLink(link, target);
        } catch (IOException | UnsupportedOperationException exception) {
            assumeTrue(false, "Symbolic links are unavailable in this test environment.");
        }
    }
}
