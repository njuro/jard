package helpers;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constants {

    public static final Path USER_CONTENT_PATH = Paths.get(System.getProperty("user.dir"), "usercontent");
    public static final String USER_CONTENT_URL = "/usercontent/";

    private Constants() {
    }
}
