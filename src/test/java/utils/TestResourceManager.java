/**
 * TestResourceManager.java
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Comparator;

/**
 * @author Simone De Cristofaro
 * Jun 11, 2018
 */
public class TestResourceManager {

    
    private TestResourceManager() {}
    
    public static File getResourceFile(Class<?> clazz, String path) throws URISyntaxException {
        return new File(clazz.getResource(path).toURI());
    }

    public static void deleteIfExists(java.nio.file.Path target) throws IOException {
        if (Files.exists(target)) {
            Files.walk(target)
                    .sorted(Comparator.reverseOrder())
                    .map(java.nio.file.Path::toFile)
                    .forEach(File::delete);
        }
    }
    
}
