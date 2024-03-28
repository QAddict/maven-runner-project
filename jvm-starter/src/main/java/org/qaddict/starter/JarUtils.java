package org.qaddict.starter;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Manifest;

public final class JarUtils {

    private JarUtils() {}

    public static String getMainClassFromManifestOfJar(String jar) throws IOException {
        JarURLConnection connection = (JarURLConnection) new URL("jar:file:" + jar + "!/").openConnection();
        Manifest manifest = connection.getManifest();
        return manifest.getMainAttributes().getValue("Main-Class");
    }

}
