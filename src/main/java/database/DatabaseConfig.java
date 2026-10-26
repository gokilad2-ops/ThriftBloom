package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized database configuration reader.
 * Loads settings from db.properties if present, or falls back to documented defaults.
 */
public class DatabaseConfig {

    private static String url = "jdbc:mysql://localhost:3306/campus_thrift?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String serverBaseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "";
    private static String driver = "com.mysql.cj.jdbc.Driver";

    static {
        loadConfig();
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try {
            // First try loading from root directory
            File file = new File("db.properties");
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                }
            } else {
                // Next try classpath
                try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (is != null) {
                        props.load(is);
                    }
                }
            }

            if (props.containsKey("db.url")) {
                url = props.getProperty("db.url").trim();
            }
            if (props.containsKey("db.user")) {
                user = props.getProperty("db.user").trim();
            }
            if (props.containsKey("db.password")) {
                password = props.getProperty("db.password").trim();
            }
            if (props.containsKey("db.driver")) {
                driver = props.getProperty("db.driver").trim();
            }

            // Derive server base URL from db.url by removing the database name
            if (url != null && url.contains("/")) {
                int lastSlash = url.lastIndexOf('/');
                int questionMark = url.indexOf('?', lastSlash);
                String params = questionMark != -1 ? url.substring(questionMark) : "";
                String baseBeforeDb = url.substring(0, lastSlash + 1);
                serverBaseUrl = baseBeforeDb + params;
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load db.properties, using built-in defaults: " + e.getMessage());
        }
    }

    public static String getUrl() { return url; }
    public static String getServerBaseUrl() { return serverBaseUrl; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }
    public static String getDriver() { return driver; }

    public static void setCredentials(String newUrl, String newUser, String newPass) {
        url = newUrl;
        user = newUser;
        password = newPass;
    }
}
